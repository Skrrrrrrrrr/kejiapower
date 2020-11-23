package main.java.com.longriver.kejiapower.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Program:Lisenter
 * Description:
 * Creation Time: 2020/11/20 15:09
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */
public class Lisenter {

//    private static ObjectOutputStream os;
//
//    public static void Send(BlockingQueue outBlockingQueue) {
//        OutputStream os;
//        ObjectOutputStream output;
//        Logger logger = LoggerFactory.getLogger(Lisenter.class)
//
//        ServerSocket serverSocket = new ServerSocket(9001);
////            serverSocket.bind(new InetSocketAddress(9001));
////        Socket socket = new Socket();
//
//        try {
//            os = serverSocket. ();
//            output = new ObjectOutputStream(os);
//            if (outBlockingQueue.size() > 0) {
//                outputString = outBlockingQueue.take();
//                if (!(outputString == null || outputString.length() <= 0)) {
//
//                    try {
//                        output.writeObject(outputString.getBytes());
//                        output.flush();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        logger.error(e.toString());
//                    }
//
//                    logger.info(Thread.currentThread().getName()
//                            + "服务器发送数据，目前总共有" + outBlockingQueue.size() + "个；");
//                }
//            }
//
//        } catch (InterruptedException e) {
////                            e.printStackTrace();
//            logger.error(e.toString());
//        } catch (IOException e) {
////            e.printStackTrace();
//            logger.error(e.toString());
//        }
//    }

}
