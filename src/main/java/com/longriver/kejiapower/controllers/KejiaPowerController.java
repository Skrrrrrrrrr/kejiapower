package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.Main;
import com.longriver.kejiapower.POJO.ClientMessage;
import com.longriver.kejiapower.POJO.Message;
import com.longriver.kejiapower.POJO.ServerMessage;
import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.model.TcpServer;
import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.OperateModel;
import com.longriver.kejiapower.utils.StringUtils;
import com.longriver.kejiapower.utils.WorkingStatus;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

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
    private ToggleGroup controlTypeToggleGroup;

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
    private TabPane powerStatusTab;

    @FXML
    private ChoiceBox<?> powerSelectedChoiceBox;

    @FXML
    private TextField portTextField;

    @FXML
    private Button powerConnectedBtn;

    @FXML
    private TableView<Client> powerTableView;

    @FXML
    private TableColumn<Client, Integer> idColumn = new TableColumn<Client, Integer>("序号");

    @FXML
    private TableColumn<Client, String> ipColumn = new TableColumn<Client, String>("设备");

    @FXML
    private TableColumn<Client, String> statusColumn = new TableColumn<Client, String>("状态");


    @FXML
    private VBox statusVbox;

    @FXML
    private Button hardwareFaultBtn;

    @FXML
    private Button otBtn;

    @FXML
    private Button inputUVBtn;

    @FXML
    private Button startupBtn;

    @FXML
    private Button commStatusbtn;

    @FXML
    private VBox modelVbox;

    @FXML
    private Button localModelBtn;

    @FXML
    private Button a50Btn;

    @FXML
    private Button a35Btn;

    @FXML
    private Button a15Btn;

    @FXML
    private Button a15_24_100Btn;


    @FXML
    void portTextFieldOnClick(ActionEvent event) {
    }

    private static final int BUFF_SIZE = 1024;
    private static final float Base = 10.0f;
    private static final float KILO = 1000.0f;
    private static final int CLIENT_AMOUNT = 8;
    private static final int ALIVE_TIME = 60000;//milliseconds


    public static Logger logger = LoggerFactory.getLogger(KejiaPowerController.class);

    private BlockingQueue<String> inBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    private BlockingQueue<String> outBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);

    private BlockingQueue<byte[]> fileInBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    //    private BlockingQueue<byte[]> fileOutBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    private BlockingQueue<Message> repaintBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);


    private TcpServer tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);//
    private ReadMessageFromClientService readMessageFromClientService = new ReadMessageFromClientService();
    private String firstStringOfInBlockingQueue = null;//

    private Map<String, XYChart.Series> clientSeriesMap = new HashMap<>();

    private ClientMessage clientMessage;
    private ServerMessage serverMessage;

    //    private List<Long> clientBirthTimeList = new ArrayList<Long>(CLIENT_AMOUNT);//Client出生时间
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

    private ObservableList<Client> clientObservableList = FXCollections.observableArrayList();
    private Map<String, Client> clientMap = new HashMap<>(CLIENT_AMOUNT);//根据Client的 IP，放入，加速遍历更新clientObservableList
    private ExecutorService pool = Executors.newFixedThreadPool(CLIENT_AMOUNT);// 线程池

    private Background defaultBtnBackground;//存储默认btn样式属性
    private String defaultColor = "#D2D2D2";


    @FXML
    private void initClientTable() {
        //绑定数据到TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        powerTableView.setItems(clientObservableList);

//        Client client = new Client();
//        client.setId(clientObservableList.size() + 1);
//        client.setIp("127.0.0.1:15195");
//        client.setStatus(WorkingStatus.getWorkingStatusByCode((short) 0x08));
//        clientObservableList.add(client);
//        client = new Client();
//        client.setId(clientObservableList.size() + 1);
//        client.setIp("127.0.0.1:15196");
//        client.setStatus(WorkingStatus.getWorkingStatusByCode((short) 0x10));
//        clientObservableList.add(client);
    }


    private VBox initChart() {

        LineChart<Number, Number> vChart;
        LineChart<Number, Number> cChart;
        LineChart<Number, Number> pChart;
        XYChart.Series voltageSeries = new XYChart.Series();
        XYChart.Series currentSeries = new XYChart.Series();
        XYChart.Series powerSeries = new XYChart.Series();
        NumberAxis vx = new NumberAxis(0, 120, 20);
        NumberAxis cx = new NumberAxis(0, 120, 20);
        NumberAxis px = new NumberAxis(0, 120, 20);

        NumberAxis vAxis = new NumberAxis(0, 1000, 100);
        NumberAxis cAxis = new NumberAxis(0, 100, 10);
        NumberAxis pAxis = new NumberAxis(0, 40, 10);
        vAxis.setTickLabelFill(Color.RED);
        cAxis.setTickLabelFill(Color.CHOCOLATE);
        pAxis.setTickLabelFill(Color.FIREBRICK);
        vAxis.setMinorTickVisible(false);
        cAxis.setMinorTickVisible(false);
        pAxis.setMinorTickVisible(false);

        vChart = new LineChart<Number, Number>(vx, vAxis);
        cChart = new LineChart<Number, Number>(cx, cAxis);
        pChart = new LineChart<Number, Number>(px, pAxis);

        vChart.setPrefHeight(200);
        vChart.setMouseTransparent(true);
        vChart.setLegendSide(Side.RIGHT);
        vChart.setLegendVisible(true);
//        voltageSeries.getData().add()
        voltageSeries.setName("Voltage(V)");
//        voltageSeries.getData().add(vData);
        vChart.setPrefWidth(530);
        vChart.setPrefHeight(300);
        vChart.getData().add(voltageSeries);
        vChart.setCreateSymbols(false);
        vChart.setAnimated(false);

        cChart.setPrefHeight(300);
        cChart.setMouseTransparent(true);
        cChart.setLegendSide(Side.RIGHT);
        cChart.setLegendVisible(true);
//        cChart.setTranslateY(-40);
        currentSeries.setName("Current(A)");
//        currentSeries.getData().add(cData);
        cChart.getData().add(currentSeries);
        cChart.setCreateSymbols(false);
        cChart.setAnimated(false);

        pChart.setPrefHeight(300);
        pChart.setMouseTransparent(true);
        pChart.setLegendSide(Side.RIGHT);
        pChart.setLegendVisible(true);
//        pChart.setTranslateY(-80);
        powerSeries.setName("Power(kW)");
//        powerSeries.getData().add(pData);
        pChart.getData().add(powerSeries);
        pChart.setCreateSymbols(false);
        pChart.setAnimated(false);

        ArrayList<XYChart.Series> seriesList = new ArrayList<>(3);
        seriesList.add(voltageSeries);
        seriesList.add(currentSeries);
        seriesList.add(powerSeries);

        VBox vBox = new VBox();
//        displayCHartVbox.setAlignment(Pos.BOTTOM_LEFT);
        vBox.getChildren().addAll(vChart, cChart, pChart);

        return vBox;
    }

    private void powerTabGenerate() {
        Tab tab = new Tab();
        tab.setId(new StringBuilder("powerTab").append(powerDisplayTab.getTabs().size() + 1).toString());
        tab.setText(new StringBuilder("电源-").append(powerDisplayTab.getTabs().size() + 1).toString());

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefHeight(600);
        anchorPane.setPrefWidth(520);
        GridPane gridPane = new GridPane();
        gridPane.setPrefWidth(600);
        gridPane.setPrefHeight(520);
        gridPane.setHgap(2);
        gridPane.setVgap(1);
        VBox vBox = new VBox();
        Label idLabel = new Label("编号：");
        idLabel.setPrefHeight(20);
        gridPane.add(idLabel, 0, 0);
        gridPane.add(initChart(), 0, 1);

        anchorPane.getChildren().add(gridPane);
        tab.setContent(anchorPane);
        powerDisplayTab.getTabs().add(tab);
    }


    private void initInteractMessage() {
        messageStringProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == "") {
                    return;
                }
                rxStringToAddSpace.setValue(StringUtils.getStringAddSpace(messageStringProperty.getValue(), 2));

                clientMessage = new ClientMessage();
                clientMessage.getClientMessage(messageStringProperty.getValue());

//                try {
//                    pool.execute(new Handler(clientMessage));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    logger.error(e.toString());
//                } finally {
//                    try {
//                        shutdownAndAwaitTermination(pool);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        logger.error(e.getMessage());
//                    }
//                }

                serverMessage = new ServerMessage();
                switch (DataFrame.dataFrameTypeClassify(clientMessage)) {
                    case HeartBeat:
                        serverMessage.generateHeartBeatMessage(clientMessage);
                        try {
                            tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(clientMessage.getClientIp().toString())).getOutputStream().write(serverMessage.toString().getBytes());
                            tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(clientMessage.getClientIp().toString())).getOutputStream().flush();
                            if (fileInBlockingQueue.size() >= BUFF_SIZE) {
                                fileInBlockingQueue.take();
                                fileInBlockingQueue.take();
                            }

                            fileInBlockingQueue.put(clientMessage.toString().getBytes());
                            fileInBlockingQueue.put(serverMessage.toString().getBytes());
                            updateClientList(clientMessage);

                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case Control:
                        //更新状态（无电压电流信息）
                        clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString())));
                        clientMap.get(clientMessage.getClientIp().toString()).setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString())));      //指向同一个Client，地址相同
                        //更新时间
                        clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setTime(System.currentTimeMillis());
                        clientMap.get(clientMessage.getClientIp().toString()).setTime(System.currentTimeMillis());      //指向同一个Client，地址相同
                        break;
                    case Report:
                        updateClient(clientMessage);
                        try {
                            Thread.currentThread().sleep(50);//！！！此处应该时一个大坑，如果（高并发下）messageStringProperty在 50ms 之内变化，数据应该接收不到
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //获取tab：根据clientIP获得存入clientMap的client和存入clientObservableList的client对应的序号：序号 == tab的index==电源ID-1。
//                        Tab tab = powerDisplayTab.getTabs().get(clientObservableList.indexOf(clientMap.get(clientMessage.getClientIp().toString())));
                        //获取tab里控件，进行画图
//                        AnchorPane anchorPane = (AnchorPane) powerDisplayTab.getSelectionModel().getSelectedItem().getContent();
                        AnchorPane anchorPane = (AnchorPane) powerDisplayTab.getTabs().get(clientObservableList.indexOf(clientMap.get(clientMessage.getClientIp().toString()))).getContent();

                        GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                        VBox vb = (VBox) gridPane.getChildren().get(1);
                        LineChart<Number, Number> lineChart = (LineChart<Number, Number>) vb.getChildren().get(0);
                        XYChart.Series<Number, Number> vSeries = (XYChart.Series<Number, Number>) lineChart.getData().get(0);
                        vSeries.getData().add(new XYChart.Data<Number, Number>(vSeries.getData().size(), Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base));
                        lineChart = (LineChart<Number, Number>) vb.getChildren().get(1);
                        XYChart.Series<Number, Number> cSeries = (XYChart.Series) lineChart.getData().get(0);
                        cSeries.getData().add(new XYChart.Data(cSeries.getData().size(), Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base));
                        lineChart = (LineChart<Number, Number>) vb.getChildren().get(2);
                        XYChart.Series<Number, Number> pSeries = (XYChart.Series) lineChart.getData().get(0);
                        pSeries.getData().add(new XYChart.Data(pSeries.getData().size(), Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base * Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base / KILO));
                        //
                        clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setTime(System.currentTimeMillis());
                        clientMap.get(clientMessage.getClientIp().toString()).setTime(System.currentTimeMillis());      //指向同一个Client，地址相同

                        break;
                }
//                            Platform.runLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (!powerObservableList.contains(clientMessage.getClientIp().toString())) {
//                                        powerObservableList.add(clientMessage.getClientIp().toString());
//                                        powerStatusObservableList.add(clientMessage.getStatus().toString());
//                                        powerIdInDisplayTab.setText(new StringBuilder(powerIdInDisplayTab.getText()).append(clientMessage.getClientIp()).toString());
//                                    }
//                                }
//                            });
            }
        });

    }

//    private class Handler extends Thread{
//        private ClientMessage cm;
//
//        public Handler(ClientMessage cm) {
//            this.cm = cm;
//        }
//
//        public ClientMessage getCm() {
//            return cm;
//        }
//
//        public void setCm(ClientMessage cm) {
//            this.cm = cm;
//        }
//
//        @Override
//        public void run() {
//            serverMessage = new ServerMessage();
//            switch (DataFrame.dataFrameTypeClassify(clientMessage)) {
//                case HeartBeat:
//                    serverMessage.generateHeartBeatMessage(clientMessage);
//                    try {
//                        tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(clientMessage.getClientIp().toString())).getOutputStream().write(serverMessage.toString().getBytes());
//                        tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(clientMessage.getClientIp().toString())).getOutputStream().flush();
//                        if (fileInBlockingQueue.size() >= BUFF_SIZE) {
//                            fileInBlockingQueue.take();
//                            fileInBlockingQueue.take();
//                        }
//
//                        fileInBlockingQueue.put(clientMessage.toString().getBytes());
//                        fileInBlockingQueue.put(serverMessage.toString().getBytes());
//                        updateClientList(clientMessage);
//                    } catch (InterruptedException | IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    break;
//                case Control:
//                    break;
//                case Report:
//                    updateClient(clientMessage);
//                    try {
//                        Thread.currentThread().sleep(50);//！！！此处应该时一个大坑，如果（高并发下）messageStringProperty在 50ms 之内变化，数据应该接收不到
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    //获取tab：根据clientIP获得存入clientMap的client和存入clientObservableList的client对应的序号：序号 == tab的index==电源ID-1。
////                        Tab tab = powerDisplayTab.getTabs().get(clientObservableList.indexOf(clientMap.get(clientMessage.getClientIp().toString())));
//                    //获取tab里控件，进行画图
////                        AnchorPane anchorPane = (AnchorPane) powerDisplayTab.getSelectionModel().getSelectedItem().getContent();
//                    AnchorPane anchorPane = (AnchorPane) powerDisplayTab.getTabs().get(clientObservableList.indexOf(clientMap.get(clientMessage.getClientIp().toString()))).getContent();
//
//                    GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
//                    VBox vb = (VBox) gridPane.getChildren().get(1);
//                    LineChart<Number, Number> lineChart = (LineChart<Number, Number>) vb.getChildren().get(0);
//                    XYChart.Series<Number, Number> vSeries = (XYChart.Series<Number, Number>) lineChart.getData().get(0);
//                    vSeries.getData().add(new XYChart.Data<Number, Number>(vSeries.getData().size(), Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base));
//                    lineChart = (LineChart<Number, Number>) vb.getChildren().get(1);
//                    XYChart.Series<Number, Number> cSeries = (XYChart.Series) lineChart.getData().get(0);
//                    cSeries.getData().add(new XYChart.Data(cSeries.getData().size(), Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base));
//                    lineChart = (LineChart<Number, Number>) vb.getChildren().get(2);
//                    XYChart.Series<Number, Number> pSeries = (XYChart.Series) lineChart.getData().get(0);
//                    pSeries.getData().add(new XYChart.Data(pSeries.getData().size(), Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base * Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base / KILO));
//                    //
//                    break;
//            }
//        }
//    }

    /*
     * 根据采样的信号message，更新tab和table里的组件
     * 包括：tab的曲线图，table里的设备列表
     * 需要：同时更新clientObservableList和clientMap
     * */
    private void updateClientList(ClientMessage clientMessage) {
        //更新ClientTable/clientMap，目前在心跳部分调用
        if (clientMap.get(clientMessage.getClientIp().toString()) == null) {
            Client ct = new Client();
            ct.setId(clientObservableList.size() + 1);
            ct.setName((new StringBuilder("电源-").append(clientObservableList.size() + 1)).toString());
            ct.setIp(StringUtils.hexStr2Ip(clientMessage.getClientIp().toString()));
            ct.setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 10)));
            ct.setTime(System.currentTimeMillis());
            clientMap.put(clientMessage.getClientIp().toString(), ct);
            clientObservableList.add(ct);
//            clientBirthTimeList.add(System.currentTimeMillis());
            if (clientMap.size() > 1) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        powerTabGenerate();//生成的tab的index和clientMap的序号一致
                    }
                });
            }
        } else {
            clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setTime(System.currentTimeMillis());
            clientMap.get(clientMessage.getClientIp().toString()).setTime(System.currentTimeMillis());      //指向同一个Client，地址相同
        }
    }

    /*
     * 根据采样的信号message，更新Client状态值
     * 包括：tab的曲线图
     * 同时更新clientObservableList和clientMap
     * */
    private void updateClient(ClientMessage clientMessage) {
        //更新ClientTable/clientMap
        if (clientMap.get(clientMessage.getClientIp().toString()) == null) {
            updateClientList(clientMessage);
            updateClient(clientMessage);
        } else {
            Client ct = clientMap.get(clientMessage.getClientIp().toString());
            ct.setVoltage(Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base);
            ct.setCurrent(Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base);
            ct.setPower(Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base * Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base / KILO);
            ct.setOperateModel(OperateModel.getOperateModelByCode(Short.parseShort(clientMessage.getModel().toString(), 10)));
            ct.setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 10)));
            if (ct.getTime() < 0) {
                ct.setTime(System.currentTimeMillis());
            }
            clientMap.replace(clientMessage.getClientIp().toString(), ct);
            clientObservableList.set(ct.getId() - 1, ct);
        }
    }

//    private ThreadPoolExecutor pool = new ThreadPoolExecutor(CLIENT_AMOUNT, CLIENT_AMOUNT, ALIVE_TIME, SECONDS, new PriorityBlockingQueue<Runnable>());

//    private void shutdownAndAwaitTermination(ExecutorService pool) {
//        pool.shutdown(); // Disable new tasks from being submitted
//        try {
//            // Wait a while for existing tasks to terminate
//            if (!pool.awaitTermination(5, SECONDS)) {
//                pool.shutdownNow(); // Cancel currently executing tasks
//                // Wait a while for tasks to respond to being cancelled
//                if (!pool.awaitTermination(5, SECONDS))
//                    logger.error("Pool did not terminate");
//            }
//        } catch (InterruptedException ie) {
//            // (Re-)Cancel if current thread also interrupted
//            pool.shutdownNow();
//            // Preserve interrupt status
//            Thread.currentThread().interrupt();
//        }
//    }


    @FXML
    private void nodePropertyArchive() {
        defaultBtnBackground = (Background) localModelBtn.getBackground();
    }

    @FXML
    private void initialize() {
        initChart();
        initInteractMessage();
        initClientTable();
        powerTabGenerate();
//        powerTabGenerate();
//        pool.allowCoreThreadTimeOut(true);
    }

    private ObservableList powerObservableList = FXCollections.observableArrayList();
    private ObservableList powerStatusObservableList = FXCollections.observableArrayList();

    @FXML
    private StringProperty messageStringProperty = new SimpleStringProperty("");
    @FXML
    private StringProperty paintStringProperty = new SimpleStringProperty("");

    private StringProperty rxStringToAddSpace = new SimpleStringProperty("");


    @FXML
    void powerConnectedBtnOnClick(ActionEvent event) {
        if (defaultBtnBackground == null) {
            nodePropertyArchive();
        }

        //        int clientThread = powerDisplayTab.getTabs().size();
        try {
            switch (powerConnectedBtn.getText()) {
                case "连接设备":
                    tcpServer.setPORT(Integer.parseInt(portTextField.getText(), 10));
                    tcpServer.start();
                    Thread.sleep(10);//服务器完全启动
                    readMessageFromClientService.start();
                    rxTextBtn.setDisable(false);
                    txTextBtn.setDisable(false);
                    logger.info("powerConnectedBtnOnClick!");
                    powerConnectedBtn.setText("断开设备");

                    //心跳包
                    heartBeatService.setPeriod(Duration.millis(ALIVE_TIME));
                    if (!heartBeatService.isRunning()) {
                        heartBeatService.start();
                    }
                    break;
                case "断开设备":
                    tcpServer.cancel();
                    tcpServer.reset();
                    readMessageFromClientService.cancel();
                    readMessageFromClientService.reset();
                    if (heartBeatService.isRunning()) {
                        heartBeatService.cancel();
                        heartBeatService.reset();
                    }

                    rxTextBtn.setDisable(true);
                    txTextBtn.setDisable(true);
                    powerConnectedBtn.setText("连接设备");
                    if (rxTextBtn.getText().equals("停止接收")) {
                        rxTextBtn.setText("开始接收");
                        rxTextArea.textProperty().unbind();
                        messageStringProperty.setValue("");
                    }
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
                rxTextArea.setText("");
                rxTextBtn.setText("停止接收");
                rxTextArea.textProperty().bind(rxStringToAddSpace);
                break;
            case "停止接收":
                rxTextBtn.setText("开始接收");
                rxTextArea.textProperty().unbind();
                messageStringProperty.setValue("");
                break;
            default:
                break;
        }
    }

    @FXML
    void txTextBtnOnClick(ActionEvent event) {
        if (txTextArea.getText() == null || txTextArea.getText().length() <= 0) {
            return;
        }
        try {
            DataFrame.invalidDataFrameCheck(txTextArea.getText().replaceAll(" +", ""));
        } catch (RuntimeException re) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("数据非法");
            alert.setHeaderText("");
            alert.setContentText("发送的数据不正确");
            final Optional<ButtonType> opt = alert.showAndWait();
            return;
        }
        ServerMessage sm = new ServerMessage();
        sm.generateControlMessage(txTextArea.getText().replaceAll(" +", ""));
        try {
            if (null != tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString()))) {
//                outBlockingQueue.put(txTextArea.getText().replaceAll(" +", ""));
//                tcpServer.getSocket().getOutputStream().write(outBlockingQueue.take().replaceAll(" +", "").getBytes());
                tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().write(txTextArea.getText().replaceAll(" +", "").getBytes());
                tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().flush();
//                tcpServer.Send(outBlockingQueue);
                updateStatusGroup(sm);
            }
        } catch (IOException | NullPointerException e) {
//            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }


    private void updateStatusGroup(ServerMessage serverMessage) {


        for (Node b : modelVbox.getChildren()) {
            Button btn = (Button) b;
//                btn.setBackground();
            btn.setBackground(defaultBtnBackground);
            btn.setFont(Font.getDefault());

        }

        switch (OperateModel.getOperateModelByCode(Short.parseShort(serverMessage.getModel().toString()))) {
            case LOCAL:
                btnStyleSet(localModelBtn);
                break;
            case DAEMON50A:
                btnStyleSet(a50Btn);
                break;
            case DAEMON35A:
                btnStyleSet(a35Btn);
                break;
            case DAEMON15A:
                btnStyleSet(a15Btn);
                break;
            case DAEMON15A_24_100:
                btnStyleSet(a15_24_100Btn);
                break;
            case INVALID:
            default:
                break;

        }
    }

    private void btnStyleSet(Button btn) {
        Background background = new Background(new BackgroundFill(Paint.valueOf(defaultColor), defaultBtnBackground.getFills().get(0).getRadii(), defaultBtnBackground.getFills().get(0).getInsets()));

        btn.setBackground(background);
        btn.setTextFill(Paint.valueOf("#000000"));
        btn.setFont(Font.font(btn.getFont().getSize() + 2));
    }

    private class ReadMessageFromClientService extends Service {
        @Override
        protected Task<Void> createTask() {

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            messageStringProperty.setValue("");
                            messageStringProperty.setValue(getInBlockingQueue().take());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            logger.error(e.getMessage());
                            Thread.currentThread().interrupt();
                        }
                    }
                    return null;
                }
            };
            return task;
        }
    }


    private ScheduledService heartBeatService = new ScheduledService() {
        protected Task createTask() {
            return new Task<Void>() {

                @Override
                protected Void call() {
//                    if (clientBirthTimeList.size() <= 0) {
//                        return null;
//                    }

                    for (Client client : clientObservableList) {
                        long currentTime = System.currentTimeMillis();

                        if (currentTime - client.getTime() > ALIVE_TIME) {
                            Client newClient = client;
                            newClient.setStatus(WorkingStatus.COMMUNICATION_TIMEOUT);
                            clientObservableList.set(clientObservableList.indexOf(client), newClient);
                            clientMap.replace(client.getIp(), newClient);
//                            clientObservableList.get(clientObservableList.indexOf(client)).setStatus(WorkingStatus.COMMUNICATION_TIMEOUT);
//                            clientMap.get(client.getId() -1 ).setStatus(WorkingStatus.COMMUNICATION_TIMEOUT);
                        } else {

                        }
                    }
                    return null;
                }
            };
        }
    };

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
//                    voltageSeries.getData().add(new XYChart.Data(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base));
//                    currentSeries.getData().add(new XYChart.Data(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base));
//                    powerSeries.getData().add(new XYChart.Data(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), (Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() * Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue()) / Base));

                Platform.runLater(() -> {//修改界面的工作
                });
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    @FXML
    void fastConfigBtnOnClick(ActionEvent event) {
        if (clientObservableList.size() <= 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("信息");
            alert.setHeaderText("未连接电源设备。");
            final Optional<ButtonType> opt = alert.showAndWait();

            return;
        }
        try {
//            Parent fastConfigRoot = FXMLLoader.load(getClass().getClassLoader().getResource("view/fxml/fastpowerconfig.fxml"));

            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource("view/fxml/fastpowerconfig.fxml");
            fxmlLoader.setLocation(url);
            Parent fastConfigRoot = fxmlLoader.load();
            FastPowerConfigController fastPowerConfigController = fxmlLoader.getController();
//            fastPowerConfigController.setClientObservableList(clientObservableList);

            Stage stage = new Stage();
            stage.setTitle("快速配置");
            stage.setScene(new Scene(fastConfigRoot));
            stage.show();
            fastPowerConfigController.getInnerClassObservableList(clientObservableList);

//            stage[0].setOnCloseRequest(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent event) {
//
//                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                    alert.setTitle("退出程序");
//                    alert.setHeaderText("");
//                    alert.setContentText("您真的要退出吗？");
//                    final Optional<ButtonType> opt = alert.showAndWait();
//                    final ButtonType rtn = opt.get();
//                    if (rtn == ButtonType.CANCEL) {
//                        event.consume();
//                    } else {
//                        stage[0].close();
//                        if (stage[0] != null) {
//                            stage[0] = null;
//                        }
//                    }
//
//                }
//            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




