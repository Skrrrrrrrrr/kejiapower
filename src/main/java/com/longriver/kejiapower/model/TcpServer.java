package com.longriver.kejiapower.model;


import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Program:TcpServer
 * Description:
 * Creation Time: 2020/11/16 18:35
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */


public class TcpServer extends Service {

    //必须实现线程，否则主界面卡死在内部类的.accept()
    /* Setting up variables */
    private int PORT;
    private static final int BUFF_SIZE = 1024;
    private static final int CLIENT_AMOUNT = 8;

    static String inputString;
    static String outputString;
    private final Logger logger = LoggerFactory.getLogger(TcpServer.class);


    private Map<String, Socket> socketMap = new HashMap<>(CLIENT_AMOUNT);

//    private static InputStream is;
    //        ObjectInputStream input = new ObjectInputStream(is);
//    private static OutputStream os;
//    private static ObjectOutputStream output ;

    //    private boolean sendFlag = false;//UI的发送指令。txBtn的text()
    static BlockingQueue<String> inBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    static BlockingQueue<String> outBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
//    static BlockingQueue<String> inBlockingQueue;
//    static BlockingQueue<String> outBlockingQueue;


    public TcpServer() {
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public TcpServer(BlockingQueue inBlockingQueue, BlockingQueue<String> outBlockingQueue) {
        this.inBlockingQueue = inBlockingQueue;
        this.outBlockingQueue = outBlockingQueue;
    }

    public String getOutputString() {
        return outputString;
    }

    public void setOutputString(String outputString) {//用来赋值发送数据
        this.outputString = outputString;
    }

    public BlockingQueue<String> getInBlockingQueue() {
        return inBlockingQueue;
    }

    public void setInBlockingQueue(BlockingQueue<String> inBlockingQueue) {
        this.inBlockingQueue = inBlockingQueue;
    }

    public BlockingQueue<String> getOutBlockingQueue() {
        return outBlockingQueue;
    }

    public void setOutBlockingQueue(BlockingQueue<String> outBlockingQueue) {
        this.outBlockingQueue = outBlockingQueue;
    }

    public Map<String, Socket> getSocketMap() {
        return socketMap;
    }

    public void setSocketMap(Map<String, Socket> socketMap) {
        this.socketMap = socketMap;
    }

    @Override
    protected Task createTask() {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                logger.info("The TCP Server is running.");
                logger.info("Current Thread is  " + Thread.currentThread());
                ServerSocket serverSocket = new ServerSocket(getPORT());
                ExecutorService pool;
                pool = Executors.newFixedThreadPool(2);
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        pool.execute(new Handler( serverSocket.accept()));
                        logger.info("Server Thread -" + Thread.currentThread() + " starts!");
                    }
                } catch (Exception e) {
                    logger.info(e.toString());
                } finally {
                    try {
                        if (null != serverSocket) {
                            serverSocket.close();
                        }
                        shutdownAndAwaitTermination(pool);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        logger.error("NullPointerException: OutputStream already closed!");
                    }
                    logger.info("closeConnections() method Exit");
                }
                return null;
            }
        };

        return task;
    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(5, TimeUnit.SECONDS))
                    logger.error("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }


    private class Handler extends Thread {
        private Socket serverSocketAccept;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private final Logger logger = LoggerFactory.getLogger(Handler.class);

        public Handler(Socket socket) throws IOException {
            this.serverSocketAccept = socket;
        }

        public void run() {
            logger.info("Current Handler Thread is  " + Thread.currentThread());
            logger.info("Server Handler Thread starts!");
            logger.info("Attempting to connect a user...");
            logger.info("User's Addr : " + serverSocketAccept.getInetAddress().getHostAddress() + ':' + serverSocketAccept.getPort());
            try {
                socketMap.put(serverSocketAccept.getInetAddress().getHostAddress(),serverSocketAccept);

                InputStream is = serverSocketAccept.getInputStream();
                OutputStream os = serverSocketAccept.getOutputStream();

//                input = new ObjectInputStream(is);//如果client没有用ObjectOutputStream发送数据，此处报错StreamCorruptedException
                output = new ObjectOutputStream(os);
                byte[] bytes = new byte[BUFF_SIZE];

                while (!Thread.currentThread().isInterrupted() && null != serverSocketAccept && serverSocketAccept.isConnected()) {
                    int len = is.read(bytes);
                    if (-1 == len) {
                        break;
                    }
                    inputString = new String(bytes, 0, len);
                    logger.info("采集到的数据是" + inputString);
                    try {
                        if (inBlockingQueue.size() >= BUFF_SIZE) {
                            inBlockingQueue.take();
                        }
                        inBlockingQueue.put(inputString);
                        logger.info(Thread.currentThread().getName()
                                + "服务器接收数据，目前总共有" + inBlockingQueue.size() + "个；");

//                        if (null != outBlockingQueue && outBlockingQueue.size()>0) {
//                            output.writeBytes(outBlockingQueue.take());
//                            output.flush();
//                        }
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        logger.info(e.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeConnections();
            }
        }


        /*
         * Once a user has been disconnected, we close the open connections and remove the writers
         */
        private synchronized void closeConnections() {
            try {
//                input.close();
                output.close();
                serverSocketAccept.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.toString());
            }

            logger.info("closeConnections() method Exit");
        }
    }
}

