package main.java.com.longriver.kejiapower.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import main.java.com.longriver.kejiapower.model.TcpServer;
import main.java.com.longriver.kejiapower.utils.DataFrame;
import main.java.com.longriver.kejiapower.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private LineChart<?, ?> cuurentChartInDisplayTab;

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
    private ChoiceBox<?> powerSellectedChoiceBox;

    @FXML
    private Button powerConnecctedBtn;

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

    private TcpServer tcpServer = null;//服务器线程
    private Thread st = null;//服务器socket.accpt()线程，需要不停的检测数据到来
    //        Thread st = new Thread(new TcpServer(inBlockingQueue, outBlockingQueue));
//    private Thread wd = null;//检测活着的Client
    private String firstStringOfInBlockingQueue = null;//

    @FXML
    void powerConnecctedBtnOnClick(ActionEvent event) {
//        int clientThread = powerDisplayTab.getTabs().size();
        try {
            switch (powerConnecctedBtn.getText()) {
                case "连接设备":
                    if (null == st) {
                        rxTextBtn.setDisable(false);
                        txTextBtn.setDisable(false);
                        tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);
                        st = new Thread(tcpServer);
                        st.start();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        firstStringOfInBlockingQueue = getInBlockingQueue().take();
                                        if (DataFrame.isHeartBeat(firstStringOfInBlockingQueue)) {
                                            tcpServer.getSocket().getOutputStream().write(DataFrame.respondHeartBeat(firstStringOfInBlockingQueue.replaceAll(" +", "")).getBytes());
                                            tcpServer.getSocket().getOutputStream().flush();
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                        logger.info("TcpServer start!");
                    } else if (!st.isAlive()) {
                        if (null == tcpServer) {
                            tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);
                        }
                        st.start();
                    } else {
                        logger.info("TcpServer working!");
                    }
                    logger.info("powerConnectedBtnOnClick!");

                    powerConnecctedBtn.setText("断开设备");
                    break;
                case "断开设备":
                    rxTextBtn.setDisable(true);
                    txTextBtn.setDisable(true);
                    if (st == null || !st.isAlive()) {
                        logger.info("TcpServer Stopped!");
                    } else {
                        st.interrupt();
                        if (null != tcpServer.getServerSocket()) {
                            tcpServer.getServerSocket().close();
                            tcpServer.setServerSocket(null);
                        }
                        st = null;
                        logger.info("TcpServer interrupted!");
                    }
//                logger.info("线程中止" + String.valueOf(st.isInterrupted()));

                    powerConnecctedBtn.setText("连接设备");
                    break;
                default:

            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    private Thread repaintThread = null;

    @FXML
    void rxTextBtnOnClick(ActionEvent event) {
        switch (rxTextBtn.getText()) {
            case "开始接收":
                //主界面刷新线程，现阶段是刷rxxTextArea
                repaintThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                if ("停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
//                                    if (getInBlockingQueue().size() > 0) {
//                                        rxTextArea.setText(StringUtils.getFileAddSpace(getInBlockingQueue().take(), 2));
//                                    }//watchdog里读取了firstStringOfInBlockingQueue
//                                    firstStringOfInBlockingQueue = getInBlockingQueue().take();
                                    if (null != firstStringOfInBlockingQueue) {
                                        rxTextArea.setText(StringUtils.getFileAddSpace(firstStringOfInBlockingQueue, 2));
                                        firstStringOfInBlockingQueue = null;
                                    }
                                }
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                            //                            rxTextArea.setText(getInBlockingQueue().take());
                        }
                    }
                });

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
        if (null == tcpServer) {
            return;
        }
        if (null == st) {
            return;
        }
        if (tcpServer.getSocket().isClosed()) {
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
}



