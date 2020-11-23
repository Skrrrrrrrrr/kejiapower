package main.java.com.longriver.kejiapower.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import main.java.com.longriver.kejiapower.POJO.TcpServer;
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

    Logger logger = LoggerFactory.getLogger(KejiaPowerController.class);

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
    //主界面刷新线程，现阶段是刷rxxTextArea
    private Thread repaintThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted() && "停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
//            while (!Thread.currentThread().isInterrupted()) {
                try {
//                    rxTextArea.clear();
                    rxTextArea.setText(StringUtils.getFileAddSpace(getInBlockingQueue().take(), 2));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                            rxTextArea.setText(getInBlockingQueue().take());
            }
        }
    });

    @FXML
    void powerConnecctedBtnOnClick(ActionEvent event) {
        try {
            switch (powerConnecctedBtn.getText()) {
                case "连接设备":
                    if (null == st) {
                        rxTextBtn.setDisable(false);
                        txTextBtn.setDisable(false);
                        tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);
                        st = new Thread(tcpServer);
                        st.start();

//                        new Handler(rxTextBtn, rxTextArea, tcpServer).start();//显示接收到的数据
                        //begin to log
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

    @FXML
    void rxTextBtnOnClick(ActionEvent event) {
        switch (rxTextBtn.getText()) {
            case "开始接收":
                rxTextBtn.setText("停止接收");
                rxTextArea.clear();
                if (null == repaintThread || !repaintThread.isInterrupted()) {
                    repaintThread.start();
                }
                break;
            case "停止接收":
                rxTextBtn.setText("开始接收");
                repaintThread.interrupt();
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
        if (!(txTextArea.getText() == null || txTextArea.getText().length() <= 0) && null != tcpServer.getSocket()) {
            try {
                outBlockingQueue.put(txTextArea.getText());
                tcpServer.getSocket().getOutputStream().write(outBlockingQueue.take().replaceAll(" +", "").getBytes());
//                tcpServer.getSocket().getOutputStream().write(txTextArea.getText().replaceAll(" +","").getBytes());
                tcpServer.getSocket().getOutputStream().flush();
//                tcpServer.Send(outBlockingQueue);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("outBlockingQueue put  InterruptedException!");
            }
        }
    }
}



