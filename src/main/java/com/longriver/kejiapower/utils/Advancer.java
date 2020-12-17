package com.longriver.kejiapower.utils;

import com.longriver.kejiapower.impl.Updatable;
import javafx.application.Platform;

import java.util.concurrent.Semaphore;

/*
worker 执行后台每帧的刷新，renderer执行图形界面的刷新。

分析
其实就是启用一个线程，该线程循环地执行：

1.用nanoTime算出每帧的用时。

2.调用worker，并查看信号量。

3.如果信号量已被释放，那么通过Platform.runLater把renderer传递给JavaFx图形线程（执行完renderer后会释放信号量）。

4.回到第1步。

这个过程后台线程起主导作用，并且在把渲染任务交给图形线程后自己可以处理下一帧（即，图形处理当前帧，后台处理下一帧，这个过程可以并行，所以要注意）。

最后那个misfire只是改变了able，让线程自己退出。用完记得熄火。

注意把JavaFx与图形相关的操作放在renderer里。

并且2个线程如果有数据共享需要格外注意。
 */


public class Advancer {
    private boolean able;
    private Updatable worker;

    public Advancer(Updatable worker){
        able = false;
        this.worker = worker;
    }

    public final synchronized void advance(Updatable renderer){
        if(able)return;//防止启动多个线程
        able = true;
        new Thread(()->{
            long timer = System.nanoTime();
            Semaphore semaphore = new Semaphore(1);
            while(able)try{
                //计算瞬间时间
                long now = System.nanoTime();
                double moment = (now - timer) * 0.000000001;
                timer = now;
                //处理事务
                worker.update(moment);
                semaphore.acquire();
                Platform.runLater(()->{
                    renderer.update(moment);
                    semaphore.release();
                });
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //记得熄火，不过线程不会马上退出。
    public final void misfire(){
        able = false;
    }
}
