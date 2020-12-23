package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.POJO.ClientMessage;
import com.longriver.kejiapower.POJO.Message;
import com.longriver.kejiapower.POJO.ServerMessage;
import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.model.TcpServer;
import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.StringUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private LineChart<String, Number> voltageChartInDisplayTab;

    @FXML
    private LineChart<String, Number> currentChartInDisplayTab;

    @FXML
    private LineChart<String, Number> powerChartInDisplayTab;

    @FXML
    private Label powerIdInDisplayTab;

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

    //    private final NumberAxis xAxisofVoltage = new NumberAxis();
//    private final NumberAxis xAxisofCurrent = new NumberAxis();
//    private final NumberAxis xAxisofPower = new NumberAxis();
//    private final NumberAxis yAxisofVoltage = new NumberAxis();
//    private final NumberAxis yAxisofCurrent = new NumberAxis();
//    private final NumberAxis yAxisofPower = new NumberAxis();
    private XYChart.Series voltageSeries = new XYChart.Series();
    private XYChart.Series currentSeries = new XYChart.Series();
    private XYChart.Series powerSeries = new XYChart.Series();


    private static final int BUFF_SIZE = 1024;
    private static final float Base = 10.0f;

    public static Logger logger = LoggerFactory.getLogger(KejiaPowerController.class);
    private BlockingQueue<String> inBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    private BlockingQueue<String> outBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);

    private BlockingQueue<byte[]> fileInBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    //    private BlockingQueue<byte[]> fileOutBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    private BlockingQueue<Message> repaintBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);

//    ExecutorService es = Executors.newFixedThreadPool(1);

    private TcpServer tcpServer = null;//
    private Thread st = null;//服务器线程.服务器socket.accpt()线程，需要不停的检测数据到来
    //        Thread st = new Thread(new TcpServer(inBlockingQueue, outBlockingQueue));
//    private Thread wd = null;//检测活着的Client
    private String firstStringOfInBlockingQueue = null;//

    private List<Client> clientList;
    private ClientMessage clientMessage;
    private ServerMessage serverMessage;

    private File file = null;//存取文件

    public KejiaPowerController() {
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
    private void initialize() {

////        defining the axes
//        xAxisofVoltage.setLowerBound(0.0);
//        xAxisofVoltage.setUpperBound(60);
//        xAxisofVoltage.setLabel("时间（s）");
//        xAxisofCurrent.setLowerBound(0.0);
//        xAxisofCurrent.setUpperBound(60);
//        xAxisofCurrent.setLabel("时间（s）");
//        xAxisofPower.setLowerBound(0.0);
//        xAxisofPower.setUpperBound(60);
//        xAxisofPower.setLabel("时间（s）");
////            final NumberAxis xAxisofVoltage = new NumberAxis();
//        yAxisofVoltage.setLowerBound(0.0);
//        yAxisofVoltage.setUpperBound(1000.0);
//        yAxisofVoltage.setLabel("电压（V）");
//        yAxisofCurrent.setLowerBound(0.0);
//        yAxisofCurrent.setUpperBound(100.0);
//        yAxisofCurrent.setLabel("电流（A）");
//        yAxisofPower.setLowerBound(0.0);
//        yAxisofPower.setUpperBound(20000.0);
//        yAxisofPower.setLabel("功率（W）");

        //creating the chart
//        voltageChartInDisplayTab = new LineChart<>(xAxisofVoltage, yAxisofVoltage);
//        voltageChartInDisplayTab.setTitle("电压");
//        voltageChartInDisplayTab.setCreateSymbols(false);
//        currentChartInDisplayTab = new LineChart<>(xAxisofCurrent, yAxisofCurrent);
//        currentChartInDisplayTab.setTitle("电流");
//        currentChartInDisplayTab.setCreateSymbols(false);
//        powerChartInDisplayTab = new LineChart<>(xAxisofPower, yAxisofPower);
//        powerChartInDisplayTab.setTitle("功率");
//        powerChartInDisplayTab.setCreateSymbols(false);
//        voltageSeries.setName("Voltage");
//        currentSeries.setName("Current");
//        powerSeries.setName("Power");

//        CategoryAxis x = new CategoryAxis();
//        currentChartInDisplayTab.getXAxis().setLabel("");
//        powerChartInDisplayTab.getXAxis().setLabel("");
        voltageChartInDisplayTab.getData().add(voltageSeries);
        currentChartInDisplayTab.getData().add(currentSeries);
        powerChartInDisplayTab.getData().add(powerSeries);
//
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
                    tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);
                    tcpServer.setPORT(Integer.parseInt(portTextField.getText(), 10));
                    (st = new Thread(tcpServer)).start();
//                    (st = new Thread(tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue))).start();
//                    st.start();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (!Thread.currentThread().isInterrupted()) {
                                logger.info("pause");
                                try {
//                                    firstStringOfInBlockingQueue = getInBlockingQueue().take();
//                                    clientMessage.getHeartBeatMessage(firstStringOfInBlockingQueue);
                                    clientMessage = new ClientMessage();
                                    clientMessage.getClientMessage(getInBlockingQueue().take());
                                    serverMessage = new ServerMessage();
                                    switch (DataFrame.dataFrameTypeClassify(clientMessage)) {
                                        case HeartBeat:
                                            serverMessage.generateHeartBeatMessage(clientMessage);
                                            tcpServer.getSocket().getOutputStream().write(serverMessage.toString().getBytes());
                                            tcpServer.getSocket().getOutputStream().flush();
//                                            if ("停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
//                                                fileWriteThread.start();//是否每次需要打开文件操作IO
//                                            }
                                            if (fileInBlockingQueue.size() >= BUFF_SIZE) {
                                                fileInBlockingQueue.take();
                                                fileInBlockingQueue.take();
                                            }
                                            fileInBlockingQueue.put(clientMessage.toString().getBytes());
                                            fileInBlockingQueue.put(serverMessage.toString().getBytes());
                                            break;
                                        case Control:
//                                            if ("停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
                                            fileWriteThread.start();//是否每次需要打开文件操作IO
//                                            }
                                            break;
                                        case Report:
                                            if ("停止接收".equals(rxTextBtn.getText()) && null != tcpServer && !tcpServer.getServerSocket().isClosed()) {
                                                repaintBlockingQueue.put(clientMessage);
//                                                fileWriteThread.start();//是否每次需要打开文件操作IO
                                            }
                                            break;
                                        default:
                                    }
                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
                                    logger.error(e.getMessage());
//                                    Thread.currentThread().interrupt();
                                } catch (IOException e) {
//                                    e.printStackTrace();
                                    logger.error(e.getMessage());
//                                    Thread.currentThread().isInterrupted();
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
                    if (null != st) {
                        try {
                            st.interrupt();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        } finally {
                            st = null;
                            logger.info("TcpServer Stopped!");
                        }
                    }
                    if (null != tcpServer.getSocket()) {
                        tcpServer.getSocket().close();
                        tcpServer.setSocket(null);
                        logger.info("TcpServer server socket.accept interrupted!");
                    }
                    if (null != tcpServer.getServerSocket()) {
                        tcpServer.getServerSocket().close();
                        tcpServer.setServerSocket(null);
                        logger.info("TcpServer server socket interrupted!");
                    }
//                logger.info("线程中止" + String.valueOf(st.isInterrupted()));
//                    es.shutdownNow();
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
        Message message = new Message();

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    message = repaintBlockingQueue.take();
                    rxTextArea.setText(StringUtils.getStringAddSpace(message.toString(), 2));
//                    voltageSeries.getData().add(new XYChart.Data<String, Number>(new SimpleDateFormat("HH:mm:ss").format(new Date()), Integer.valueOf(message.getVoltage().toString(), 16).floatValue() / Base));
//                    currentSeries.getData().add(new XYChart.Data<String, Number>(new SimpleDateFormat("HH:mm:ss").format(new Date()), Integer.valueOf(message.getCurrent().toString(), 16).floatValue() / Base));

                    Platform.runLater(new Runnable() {//此处Runnable内部使用runlater，代码需要优化
                        @Override
                        public void run() {
                            //更新JavaFX的主线程的代码放在此处
                            voltageSeries.getData().add(new XYChart.Data<String, Number>(new SimpleDateFormat("HH:mm:ss").format(new Date()), Integer.valueOf(message.getVoltage().toString(), 16).floatValue() / Base));
                            currentSeries.getData().add(new XYChart.Data<String, Number>(new SimpleDateFormat("HH:mm:ss").format(new Date()), Integer.valueOf(message.getCurrent().toString(), 16).floatValue() / Base));
                            powerSeries.getData().add(new XYChart.Data<String, Number>(new SimpleDateFormat("HH:mm:ss").format(new Date()), Integer.valueOf(message.getVoltage().toString(), 16).floatValue() / Base * Integer.valueOf(message.getCurrent().toString(), 16).floatValue() / Base));
//                            voltageSeries.getData().add(new XYChart.Data<Number, Number>(count++, Integer.valueOf(message.getVoltage().toString(), 16).floatValue() / Base));
//                            currentSeries.getData().add(new XYChart.Data<Number, Number>(count++, Integer.valueOf(message.getCurrent().toString(), 16).floatValue() / Base));
//                            powerSeries.getData().add(new XYChart.Data<Number, Number>(count++, Integer.valueOf(message.getVoltage().toString(), 16).floatValue() / Base * Integer.valueOf(message.getCurrent().toString(), 16).floatValue() / Base));
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());

                }
            }
        }
    });

    private Thread fileWriteThread = new Thread(new Runnable() {

        FileOutputStream output = null;
        byte[] bytes = new byte[BUFF_SIZE];

//        {
//            try {
//                file = new File("./logs/runtime.log");
//                if (!file.exists()) file.createNewFile();
//                FileOutputStream output = new FileOutputStream(file);
//            } catch (
//                    IOException ie) {
//                logger.error(ie.getMessage());
//            }
//        }


        @Override
        public void run() {
            try {
                try {
                    file = new File("./logs/runtime.log");
                    if (!file.exists()) file.createNewFile();
                    output = new FileOutputStream(file);
                } catch (
                        IOException ie) {
                    logger.error(ie.getMessage());
                }
                bytes = new StringBuilder(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())).append(clientMessage).toString().getBytes();
                output.write(bytes);                //将数组的信息写入文件中
                bytes = new StringBuilder(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())).append(serverMessage).toString().getBytes();
                output.write(bytes);                //将数组的信息写入文件中
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    output.flush();
                    output.close();

                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
        }
    });

    private Thread fileReadThread = new Thread(new Runnable() {

        byte[] bytes = new byte[BUFF_SIZE];
        BufferedInputStream bufferedInput = null;

//        {
//            try {
//                if (!file.exists()) file.createNewFile();
//                BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file));
//            } catch (IOException ie) {
//                logger.error(ie.getMessage());
//            }
//        }

        @Override
        public void run() {
            try {
                if (!file.exists()) file.createNewFile();
                BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file));
                fileInBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
            } catch (IOException ie) {
                logger.error(ie.getMessage());
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    while (bufferedInput.read(bytes) != -1) {
                        fileInBlockingQueue.put(bytes);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                } finally {
                    try {
                        bufferedInput.close();
                        Thread.currentThread().interrupt();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    });

    //    public void refreshLineChart(LineChart chart, XYChart.Series series) {
//        chart.getData().add(series);
//    }
//


    //内部类，画图
    private class PaintBrush implements Runnable {

        @Override
        public void run() {
//            long nowTime = System.currentTimeMillis();
//            Calendar cal = Calendar.getInstance();
//            cal.setTimeInMillis(nowTime);

            XYChart.Data data = new XYChart.Data();

            try {
                if ("停止接收".equals(rxTextBtn.getText())) {
                    rxTextArea.setText(StringUtils.getStringAddSpace(clientMessage.toString(), 2));
                }
                //Linecharts
                voltageSeries.getData().add(new XYChart.Data(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base));
                currentSeries.getData().add(new XYChart.Data(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base));
                powerSeries.getData().add(new XYChart.Data(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), (Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() * Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue()) / Base));

                Platform.runLater(() -> {//修改界面的工作
                    voltageChartInDisplayTab.getData().add(voltageSeries);
                    currentChartInDisplayTab.getData().add(currentSeries);
                    powerChartInDisplayTab.getData().add(powerSeries);
                });
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}




