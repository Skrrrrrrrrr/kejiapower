package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.POJO.ClientMessage;
import com.longriver.kejiapower.POJO.ServerMessage;
import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.POJO.Message;
import com.longriver.kejiapower.model.TcpServer;
import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.DataFrameType;
import com.longriver.kejiapower.utils.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class KejiaPowerController {

    @FXML
    private GridPane txrxGrid;

    @FXML
    private TextArea rxTextArea;

    @FXML
    private TextArea txTextArea;

    @FXML
    private Button rxTextBtn;

    @FXML
    private Button txTextBtn;

    @FXML
    private GridPane testGrid;

    @FXML
    private TabPane testTab;

    @FXML
    private Tab quickTestTab;

    @FXML
    private RadioButton remoteRadioBtn;

    @FXML
    private RadioButton localRadioBtn;

    @FXML
    private Button startBtn;

    @FXML
    private Button configBtn;

    @FXML
    private Tab autotestTab;

    @FXML
    private GridPane gridOfDisplayTab;

    @FXML
    private TabPane powerDisplayTab;

    @FXML
    private Tab tab1;

    @FXML
    private LineChart<?, ?> voltageChartInDisplayTab;

    @FXML
    private LineChart<?, ?> currentChartInDisplayTab;

    @FXML
    private LineChart<?, ?> powerChartInDisplayTab;

    @FXML
    private Label powerIdInDisplayTab;

    @FXML
    private Label powerStatusInDisplayTab;

    @FXML
    private TabPane powerStatusTab;

    @FXML
    private ListView<?> powerList;

    @FXML
    private ListView<?> powerStatusList;

    @FXML
    private ChoiceBox<?> powerSelectedChoiceBox;

    @FXML
    private Button powerConnectedBtn;

    @FXML
    private TextField portTextField;

    @FXML
    void portTextFieldOnClick(ActionEvent event) {
    }


    public KejiaPowerController() {
    }

    public static Logger logger = LoggerFactory.getLogger(KejiaPowerController.class);
    private BlockingQueue<String> inBlockingQueue = new ArrayBlockingQueue<>(1024);
    private BlockingQueue<String> outBlockingQueue = new ArrayBlockingQueue<>(1024);

    private TcpServer tcpServer = null;//服务器线程
    private Thread st = null;//服务器socket.accpt()线程，需要不停的检测数据到来
    //        Thread st = new Thread(new TcpServer(inBlockingQueue, outBlockingQueue));
//    private Thread wd = null;//检测活着的Client
    private String firstStringOfInBlockingQueue = null;//

    private List<Client> clientList;
    private ClientMessage clientMessage;
    private ServerMessage serverMessage;


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

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public ClientMessage getClientMessage() {
        return clientMessage;
    }

    public void setClientMessage(ClientMessage clientMessage) {
        this.clientMessage = clientMessage;
    }

    public ServerMessage getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(ServerMessage serverMessage) {
        this.serverMessage = serverMessage;
    }

    @FXML
    void powerConnectedBtnOnClick(ActionEvent event) {
//        int clientThread = powerDisplayTab.getTabs().size();
        try {
            switch (powerConnectedBtn.getText()) {
                case "连接设备":
                    if (null != st) {
                        st = null;
                    }

                    rxTextBtn.setDisable(false);
                    txTextBtn.setDisable(false);
//                    tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);
                    (st = new Thread(tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue))).start();
//                    st.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!Thread.currentThread().isInterrupted()) {
                                try {
//                                    firstStringOfInBlockingQueue = getInBlockingQueue().take();
//                                    clientMessage.getHeartBeatMessage(firstStringOfInBlockingQueue);
                                    clientMessage = new ClientMessage(getInBlockingQueue().take());
                                    switch (DataFrame.dataFrameTypeClassify(clientMessage)) {
                                        case HeartBeat:
                                            serverMessage.generateHeartBeatMessage(clientMessage);
                                            tcpServer.getSocket().getOutputStream().write(serverMessage.toString().getBytes());
                                            tcpServer.getSocket().getOutputStream().flush();
                                            fileIOThread.start();//是否每次需要打开文件操作IO

                                            break;
                                        case Control:
                                            fileIOThread.start();//是否每次需要打开文件操作IO
                                            break;
                                        case Report:
                                            if ("停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
                                                repaintThread.start();
                                                fileIOThread.start();//是否每次需要打开文件操作IO
                                            }
                                            break;
                                        default:
                                    }
//                                    if (DataFrame.dataFrameTypeClassify(clientMessage).equals(DataFrameType.HeartBeat)) {
//                                        serverMessage.generateHeartBeatMessage(clientMessage);
//                                        tcpServer.getSocket().getOutputStream().write(serverMessage.toString().getBytes());
//                                        tcpServer.getSocket().getOutputStream().flush();
//                                    }
                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
                                    logger.error(e.getMessage());
                                    Thread.currentThread().interrupt();
                                } catch (IOException e) {
//                                    e.printStackTrace();
                                    logger.error(e.getMessage());
                                    Thread.currentThread().isInterrupted();
                                }
                            }
                        }
                    }).start();
                    logger.info("TcpServer start!");
                    logger.info("powerConnectedBtnOnClick!");
                    powerConnectedBtn.setText("断开设备");
                    break;
                case "断开设备":
                    rxTextBtn.setDisable(true);
                    txTextBtn.setDisable(true);
                    if (null == st || !st.isAlive()) {
                        logger.info("TcpServer Stopped!");
                    } else {
                        st.interrupt();
                        if (null != tcpServer.getSocket()) {
                            tcpServer.getSocket().close();
                        }
                        if (null != tcpServer.getServerSocket()) {
                            tcpServer.getServerSocket().close();
                            tcpServer.setServerSocket(null);
                        }
                        st = null;
                        logger.info("TcpServer interrupted!");
                    }
//                logger.info("线程中止" + String.valueOf(st.isInterrupted()));

                    powerConnectedBtn.setText("连接设备");
                    break;
                default:
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }

    }

    @FXML
    void rxTextBtnOnClick(ActionEvent event) {
        switch (rxTextBtn.getText()) {
            case "开始接收":
                rxTextBtn.setText("停止接收");
                rxTextArea.clear();
                repaintThread.start();
//                rxTextArea.setText(DataFrame.respondHeartBeat("FFFF0A00000401020304DD"));
                break;
            case "停止接收":
                rxTextBtn.setText("开始接收");
                if (null != repaintThread) {
                    repaintThread.interrupt();
                }
                break;
            default:
                break;
        }
    }

    @FXML
    void txTextBtnOnClick(ActionEvent event) {
        if (null == st) {
            return;
        }
        if (null == tcpServer) {
            return;
        }
        if (null == tcpServer.getServerSocket()) {
            return;
        }
        if (null == tcpServer.getSocket()) {
            return;
        }

        if (!(txTextArea.getText() == null || txTextArea.getText().length() <= 0) && null != tcpServer.getSocket()) {
            try {
//                outBlockingQueue.put(txTextArea.getText().replaceAll(" +", ""));
//                tcpServer.getSocket().getOutputStream().write(outBlockingQueue.take().replaceAll(" +", "").getBytes());
                tcpServer.getSocket().getOutputStream().write(txTextArea.getText().replaceAll(" +", "").getBytes());
                tcpServer.getSocket().getOutputStream().flush();
//                tcpServer.Send(outBlockingQueue);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }

    //主界面刷新线程，现阶段是刷rxxTextArea
    private Thread repaintThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                if ("停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
                    rxTextArea.setText(StringUtils.getFileAddSpace(clientMessage.toString(), 2));
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    });
    private Thread fileIOThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                if ("停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
                    rxTextArea.setText(StringUtils.getFileAddSpace(clientMessage.toString(), 2));
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    });

}



