package com.longriver.kejiapower.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Program:TcpServer
 * Description:
 * Creation Time: 2020/11/16 18:35
 * author wangqi
 * Email:wangq.cn@hotmail.com
 * Since kejiapower
 */


public class TcpServer implements Runnable {

    //必须实现线程，否则主界面卡死在内部类的.accept()
    /* Setting up variables */
    private static final int PORT = 9001;
    static String inputString;
    static String outputString;
    private final Logger logger = LoggerFactory.getLogger(TcpServer.class);


    private Socket socket;
    private ServerSocket serverSocket;

//    private static InputStream is;
    //        ObjectInputStream input = new ObjectInputStream(is);
//    private static OutputStream os;
//    private static ObjectOutputStream output ;

    //    private boolean sendFlag = false;//UI的发送指令。txBtn的text()
    static BlockingQueue<String> inBlockingQueue = new ArrayBlockingQueue<>(1024);
    static BlockingQueue<String> outBlockingQueue = new ArrayBlockingQueue<>(1024);
//    static BlockingQueue<String> inBlockingQueue;
//    static BlockingQueue<String> outBlockingQueue;


    public TcpServer() {
    }

    public TcpServer(BlockingQueue inBlockingQueue, BlockingQueue<String> outBlockingQueue) {
        this.inBlockingQueue = inBlockingQueue;
        this.outBlockingQueue = outBlockingQueue;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getOutputString() {
        return outputString;
    }

    public void setOutputString(String outputString) {//用来赋值发送数据
        this.outputString = outputString;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
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


    public void startServer() throws Exception {
        logger.info("The TCP Server is running.");
        serverSocket = new ServerSocket(PORT);

        try {
            while (!Thread.currentThread().isInterrupted()) {
                new Handler(socket = serverSocket.accept()).start();
                logger.info("Server Thread starts!");
//                if (Thread.currentThread().isInterrupted()) {
//                    logger.info("TcpServer Thread interrupted");
//                    serverSocket.close();
//                    break;
//                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
            logger.info(e.toString());
//            Thread.currentThread().interrupt();
        } finally {
            try {
                if (null != serverSocket) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            } catch (NullPointerException e) {
                e.printStackTrace();
                logger.error("NullPointerException: OutputStream already closed!");
            }
            logger.debug("closeConnections() method Exit");
        }
    }

    @Override
    public void run() {
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static class Handler extends Thread {
        private Socket serverSocketAccept;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private final Logger logger = LoggerFactory.getLogger(Handler.class);

        public Handler(Socket socket) throws IOException {
            this.serverSocketAccept = socket;
        }

        public void run() {
            logger.info("Server Handler Thread starts!");
            logger.info("Attempting to connect a user...");
            logger.info("User's Addr : " + serverSocketAccept.getInetAddress().getHostAddress() + ':' + serverSocketAccept.getPort());
            try {
                InputStream is = serverSocketAccept.getInputStream();
                OutputStream os = serverSocketAccept.getOutputStream();

//                input = new ObjectInputStream(is);//如果client没有用ObjectOutputStream发送数据，此处报错StreamCorruptedException
                output = new ObjectOutputStream(os);
                byte[] bytes = new byte[1024];

                while (!Thread.currentThread().isInterrupted() && null != serverSocketAccept && serverSocketAccept.isConnected()) {
                    int len = is.read(bytes);
                    if (-1 == len) {
                        break;
                    }
                    inputString = new String(bytes, 0, len);
                    logger.info("采集到的数据是" + inputString);
                    try {
                        if (inBlockingQueue.size() >= 1024) {
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

            logger.debug("closeConnections() method Exit");
        }
    }
}

