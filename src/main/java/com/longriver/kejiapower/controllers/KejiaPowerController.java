package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.POJO.ClientMessage;
import com.longriver.kejiapower.POJO.ServerMessage;
import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.model.InnerClient;
import com.longriver.kejiapower.model.TcpServer;
import com.longriver.kejiapower.utils.Control;
import com.longriver.kejiapower.utils.*;
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
import javafx.geometry.Pos;
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
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private Button fastStartBtn;

    @FXML
    private Button fastConfigBtn;

    @FXML
    private Tab autotestTab;

    @FXML
    private Button autoStartBtn;

    @FXML
    private Button autoConfigBtn;

    @FXML
    private GridPane gridOfDisplayTab;

    @FXML
    private TabPane powerDisplayTabpane;

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

    private TcpServer tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);//
    private ReadMessageFromClientService readMessageFromClientService = new ReadMessageFromClientService();
    private FileWriteService fileWriteService = new FileWriteService();
    private BlockingQueue<Client> fileOutBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);

    private ClientMessage clientMessage;
    private ServerMessage serverMessage;

    //    private ObservableList<Client> clientSetUpObservableList = FXCollections.observableArrayList();//配置界面传回来的ClientList
    private Map<Client, Control> clientControlMap = new HashMap<>();//主界面、配置界面之间传送Client
    private List<WorkingStatus> workingStatusListForClientsRecieved = new ArrayList<>(CLIENT_AMOUNT);//存储所接入的client工作状态，更新状态按钮（没有采用多页按钮组合的方式）
    private List<OperateModel> operateModelListForClientControlled = new ArrayList<>(CLIENT_AMOUNT);//同workingStatusList


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
        powerTableView.setPlaceholder(new Label(""));
        powerTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


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

        if (voltageSeries.getData().size() > 720) {//5/s,一小时
            vx.setLowerBound(vx.getLowerBound() + 1);
            cx.setLowerBound(cx.getLowerBound() + 1);
            px.setLowerBound(px.getLowerBound() + 1);
            vx.setUpperBound(vx.getUpperBound() + 1);
            cx.setUpperBound(cx.getUpperBound() + 1);
            px.setUpperBound(px.getUpperBound() + 1);

        }

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
        tab.setId(new StringBuilder("powerTab").append(powerDisplayTabpane.getTabs().size() + 1).toString());
        tab.setText(new StringBuilder("电源-").append(powerDisplayTabpane.getTabs().size() + 1).toString());

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
        powerDisplayTabpane.getTabs().add(tab);
    }


    private void initInteractMessage() {
        //接收到客户端数据后的处理
        messageStringProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue == "") {
                    return;
                }
                rxStringToAddSpace.setValue(StringUtils.getStringAddSpace(messageStringProperty.getValue(), 2));

                clientMessage = new ClientMessage();
                clientMessage.getClientMessage(messageStringProperty.getValue());

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
                        clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 16)));
                        clientMap.get(clientMessage.getClientIp().toString()).setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 16)));      //指向同一个Client，地址相同
                        //更新时间
                        clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));
                        clientMap.get(clientMessage.getClientIp().toString()).setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));      //指向同一个Client，地址相同
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
                        AnchorPane anchorPane = (AnchorPane) powerDisplayTabpane.getTabs().get(clientObservableList.indexOf(clientMap.get(clientMessage.getClientIp().toString()))).getContent();

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
                        clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));
                        clientMap.get(clientMessage.getClientIp().toString()).setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));      //指向同一个Client，地址相同
//                        updateStatusGroup(clientMessage);
                        workingStatusListForClientsRecieved.set(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1, clientMap.get(clientMessage.getClientIp().toString()).getStatus());
                        updateStatusGroup(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1, clientMap.get(clientMessage.getClientIp().toString()).getStatus());
                        try {
                            fileOutBlockingQueue.put(clientMap.get(clientMessage.getClientIp().toString()));
//                            logger.info("fileOutBlockingQueue有：" + fileOutBlockingQueue.size() + "个数据！");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }

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
            ct.setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 16)));
            ct.setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));
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
            clientObservableList.get(clientMap.get(clientMessage.getClientIp().toString()).getId() - 1).setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));
            clientMap.get(clientMessage.getClientIp().toString()).setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));      //指向同一个Client，地址相同
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
            ct.setOperateModel(OperateModel.getOperateModelByCode(Short.parseShort(clientMessage.getModel().toString(), 16)));
            ct.setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 16)));
            ct.setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));
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
        initStatusBtn();
        powerDispalyTabInit();
    }

    private void initStatusBtn() {
        for (int i = 0; i < CLIENT_AMOUNT; i++) {
            workingStatusListForClientsRecieved.add(WorkingStatus.UNKNOWN);
            operateModelListForClientControlled.add(OperateModel.INVALID.INVALID);
        }
    }

    private void powerDispalyTabInit() {
        powerDisplayTabpane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        updateStatusGroup(powerDisplayTabpane.getTabs().indexOf(t1), workingStatusListForClientsRecieved.get(powerDisplayTabpane.getTabs().indexOf(t1)));
                        updateStatusGroup(powerDisplayTabpane.getTabs().indexOf(t1), operateModelListForClientControlled.get(powerDisplayTabpane.getTabs().indexOf(t1)));
                    }
                }
        );
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
        try {
            switch (powerConnectedBtn.getText()) {
                case "连接设备":
                    for (int i = 0; i < clientMap.size(); i++) {
                        AnchorPane anchorPane = (AnchorPane) powerDisplayTabpane.getTabs().get(i).getContent();
                        GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                        VBox vb = (VBox) gridPane.getChildren().get(1);
                        LineChart<Number, Number> lineChart = (LineChart<Number, Number>) vb.getChildren().get(0);
                        XYChart.Series<Number, Number> vSeries = (XYChart.Series<Number, Number>) lineChart.getData().get(0);
                        lineChart = (LineChart<Number, Number>) vb.getChildren().get(1);
                        XYChart.Series<Number, Number> cSeries = (XYChart.Series) lineChart.getData().get(0);
                        lineChart = (LineChart<Number, Number>) vb.getChildren().get(2);
                        XYChart.Series<Number, Number> pSeries = (XYChart.Series) lineChart.getData().get(0);
                        vSeries.getData().clear();
                        cSeries.getData().clear();
                        pSeries.getData().clear();
                    }
                    clientMap.clear();
                    clientObservableList.clear();
                    if (powerDisplayTabpane.getTabs().size() > 1) {
                        powerDisplayTabpane.getTabs().remove(1, powerDisplayTabpane.getTabs().size());
                    }

                    tcpServer.setPORT(Integer.parseInt(portTextField.getText(), 10));
                    tcpServer.start();
                    Thread.sleep(10);//服务器完全启动
//                    readMessageFromClientService.start();
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
//                    readMessageFromClientService.cancel();
//                    readMessageFromClientService.reset();
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
                        rxTextArea.setText("");
                        messageStringProperty.unbind();
                        messageStringProperty.setValue("");
                        readMessageFromClientService.cancel();
                        readMessageFromClientService.reset();
//                        messageStringProperty.setValue("");
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
                messageStringProperty.bind(messageFromClientService);
                readMessageFromClientService.start();
                fileWriteService.start();

                break;
            case "停止接收":
                rxTextBtn.setText("开始接收");
                rxTextArea.textProperty().unbind();
                rxTextArea.setText("");
                messageStringProperty.unbind();
                messageStringProperty.setValue("");
                readMessageFromClientService.cancel();
                readMessageFromClientService.reset();
                fileWriteService.cancel();
                fileWriteService.reset();
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
//                updateStatusGroup(sm);
                operateModelListForClientControlled.set(clientMap.get(sm.getClientIp()).getId() - 1, clientMap.get(sm.getClientIp()).getOperateModel());

            }
        } catch (IOException | NullPointerException e) {
//            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    private void updateStatusGroup(int index, WorkingStatus workingStatus) {

        for (Node b : statusVbox.getChildren()) {
            Button btn = (Button) b;
            btn.setBackground(defaultBtnBackground);
            btn.setFont(Font.getDefault());
            btn.setTextFill(Paint.valueOf("#000000"));
        }
        switch (workingStatus) {
            case HARDWARE_FAULT:
                btnStyleSet(hardwareFaultBtn);
                break;
            case OT:
                btnStyleSet(otBtn);
                break;
            case INPUT_UV:
                btnStyleSet(inputUVBtn);
                break;
            case SHUTOFF:
                btnStyleSet(startupBtn);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        startupBtn.setText("停止");
                    }
                });
                break;
            case STARTUP:
                Background background = new Background(new BackgroundFill(Paint.valueOf(defaultColor), defaultBtnBackground.getFills().get(0).getRadii(), defaultBtnBackground.getFills().get(0).getInsets()));
                startupBtn.setBackground(background);
                startupBtn.setTextFill(Paint.valueOf("#000000"));
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        startupBtn.setText("运行");
                    }
                });
                break;
            case COMMUNICATION_TIMEOUT:
                btnStyleSet(commStatusbtn);
                break;
            case UNKNOWN:

            default:
                break;

        }
    }

    private void updateStatusGroup(int index, OperateModel operateModel) {

        for (Node b : modelVbox.getChildren()) {
            Button btn = (Button) b;
            btn.setBackground(defaultBtnBackground);
            btn.setFont(Font.getDefault());
            btn.setTextFill(Paint.valueOf("#000000"));
        }

        switch (operateModel) {
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
        btn.setTextFill(Paint.valueOf("#00993d"));
//        btn.setFont(Font.font(btn.getFont().getSize() + 2));
    }

    private SimpleStringProperty messageFromClientService = new SimpleStringProperty();

    private class ReadMessageFromClientService extends Service {
        @Override
        protected Task<Void> createTask() {

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            messageFromClientService.setValue("");
                            Thread.sleep(100);
                            messageFromClientService.setValue(getInBlockingQueue().take());
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                            logger.info("ReadMessageFromClientService$Call method interrupted!");
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

                        try {
                            if (currentTime - (new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS")).parse(client.getTime()).getTime() > ALIVE_TIME) {
                                Client newClient = (Client) client.clone();
                                newClient.setStatus(WorkingStatus.COMMUNICATION_TIMEOUT);
                                clientObservableList.set(clientObservableList.indexOf(client), newClient);
                                clientMap.replace(client.getIp(), newClient);
                                //                            clientObservableList.get(clientObservableList.indexOf(client)).setStatus(WorkingStatus.COMMUNICATION_TIMEOUT);
                                //                            clientMap.get(client.getId() -1 ).setStatus(WorkingStatus.COMMUNICATION_TIMEOUT);
                            } else {

                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            };
        }
    };


    private class FileWriteService extends Service {

        //        private FileOutputStream output = null;
        private File file = null;//存取文件
        BufferedWriter bw = null;
        private final Field[] fields = Client.class.getDeclaredFields();


        @Override
        protected Task createTask() {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {

                    //创建一个文件
                    file = new File(String.format("./logs//%s.txt", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())));
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
                    while (!isCancelled()) {
//                        Thread.sleep(100);
                        Client client = fileOutBlockingQueue.take();
                        //追加数据
                        StringBuilder sb = new StringBuilder();
                        sb.append(new SimpleDateFormat("yyyyMMdd HH:mm:ss SSS").format(new Date()));
                        sb.append(",");
                        for (Field field : fields) {

                            try {
                                // 获取原来的访问控制权限
                                boolean accessFlag = field.isAccessible();
                                // 修改访问控制权限
                                field.setAccessible(true);
                                // 获取在对象f中属性fields[i]对应的对象中的变量
                                Object o;
                                try {
                                    o = field.get(client);
                                    if (o != null) {
                                        sb.append(o.toString());
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                                // 恢复访问控制权限
                                field.setAccessible(accessFlag);
                                sb.append(",");

                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                        }
                        sb.append("\r\n");
                        bw.write(sb.toString());
                        bw.flush();
                    }
                    return null;
                }
            };
            return task;
        }

        @Override
        protected void cancelled() {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.cancelled();
        }
    }

//    private Thread fileReadThread = new Thread(new Runnable() {
//
//        byte[] bytes = new byte[BUFF_SIZE];
//        BufferedInputStream bufferedInput = null;
//
////        {
////            try {
////                if (!file.exists()) file.createNewFile();
////                BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file));
////            } catch (IOException ie) {
////                logger.error(ie.getMessage());
////            }
////        }
//
//        @Override
//        public void run() {
//            try {
//                if (!file.exists()) file.createNewFile();
//                BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file));
//                fileInBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
//            } catch (IOException ie) {
//                logger.error(ie.getMessage());
//            }
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    while (bufferedInput.read(bytes) != -1) {
//                        fileInBlockingQueue.put(bytes);
//                    }
//                } catch (Exception e) {
//                    logger.error(e.getMessage());
//                } finally {
//                    try {
//                        bufferedInput.close();
//                        Thread.currentThread().interrupt();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        }
//    });


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
        clientControlMap.clear();
        for (Client ct : clientMap.values()) {
            clientControlMap.put(ct, Control.INVALID);
        }

        try {
//            Parent fastConfigRoot = FXMLLoader.load(getClass().getClassLoader().getResource("view/fxml/fastTestPowerConfig.fxml"));

            FXMLLoader fxmlLoader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource("view/fxml/fastTestPowerConfig.fxml");
            fxmlLoader.setLocation(url);
            Parent fastConfigRoot = fxmlLoader.load();
            FastPowerConfigController fastPowerConfigController = fxmlLoader.getController();
            fastPowerConfigController.setInnerClassObservableList(clientControlMap);
            clientControlMap = fastPowerConfigController.getClientControlMap();

            Stage stage = new Stage();
            stage.setTitle("快速配置");
            stage.setScene(new Scene(fastConfigRoot));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startBtnOnClick(ActionEvent event) {
        if (clientControlMap.size() <= 0) {
            return;
        }
//        if (powerConnectedBtn.getText().equals("连接设备")) {

        if (!(tcpServer.isRunning() || readMessageFromClientService.isRunning() || heartBeatService.isRunning())) {
//            powerConnectedBtnOnClick(event);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("设备开启监听服务");
            alert.setHeaderText("");
            alert.setContentText("连接服务尚未打开");
            final Optional<ButtonType> opt = alert.showAndWait();
            return;
        }

        for (Client ct : clientControlMap.keySet()) {
            ServerMessage sm = new ServerMessage();
            sm.generateControlMessage(ct);
//            sm.setIdentification();
            sm.setControl(new StringBuilder(String.format("%02X", clientControlMap.get(ct).getCode())));
            try {
                try {
                    tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().write(sm.toString().getBytes());
                    tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().flush();
                    if (fileInBlockingQueue.size() >= BUFF_SIZE) {
                        fileInBlockingQueue.take();
                        fileInBlockingQueue.take();
                    }

                    fileInBlockingQueue.put(sm.toString().getBytes());
                    fileInBlockingQueue.put(sm.toString().getBytes());
//                    updateStatusGroup(sm);
                    operateModelListForClientControlled.set(ct.getId() - 1, ct.getOperateModel());

                } catch (NullPointerException ne) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("未找到连接的设备");
                    alert.setHeaderText("");
                    alert.setContentText(ct.getName() + "连接服务尚未打开");
                    final Optional<ButtonType> opt = alert.showAndWait();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<List<InnerClient>> innerClientsArrayListForAutoTest = new ArrayList<>(CLIENT_AMOUNT);//自动测试数据：认为状态不同的同一地址Client为不同的Client实体

    //自动测试页面
    @FXML
    void autoConfigBtnOnClick(ActionEvent event) {
        if (clientObservableList.size() <= 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("电源未连接");
            alert.setHeaderText("");
            alert.setContentText("没有找到连接的设备！");
            final Optional<ButtonType> opt = alert.showAndWait();
            return;
        }
        innerClientsArrayListForAutoTest.clear();
        try {
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefSize(600, 500);
            VBox vBox = new VBox();
            TabPane tabPane = new TabPane();
            tabPane.setPrefSize(600, 500);
            for (Client c : clientObservableList) {

                FXMLLoader fxmlLoader = new FXMLLoader();
                URL url = getClass().getClassLoader().getResource("view/fxml/autoTestPowerTab.fxml");
                fxmlLoader.setLocation(url);
                Parent autoTestPowerConfigRoot = fxmlLoader.load();
//                Parent autoTestPowerConfigRoot = FXMLLoader.load(getClass().getClassLoader().getResource("view/fxml/autoTestPowerTab.fxml"));
                Tab tab = new Tab(c.getName(), autoTestPowerConfigRoot);
                tab.setClosable(false);
                tab.setId(new StringBuilder("powerTab") + new StringBuilder((c.getId() - 1)).toString());
                tabPane.getTabs().add(tab);
                AutoTestPowerTabController autoTestPowerTabController = fxmlLoader.getController();
                autoTestPowerTabController.clientToInnerClientPara(c);
                innerClientsArrayListForAutoTest.add(autoTestPowerTabController.generateInnerClientArrayListForAutoTest());
            }
            GridPane gridPane = new GridPane();
//            Button addTabBtn = new Button("增加");
//            Button delTabBtn = new Button("删除");
            Button autoConfigBtn = new Button("配置");
            Button returnBtn = new Button("返回");
//            addTabBtn.setPrefWidth(100);
//            delTabBtn.setPrefWidth(100);
            autoConfigBtn.setPrefWidth(100);
            returnBtn.setPrefWidth(100);
            //////////////////////////

//            addTabBtn.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    System.out.println("Hello World!");
//                }
//            });
//            delTabBtn.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//
//                }
//            });
            autoConfigBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    for (Tab t : tabPane.getTabs()) {

                    }
                }
            });
            returnBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ((Stage) anchorPane.getScene().getWindow()).close();
                }
            });
            ///////////////////
            //设置网格位置
//            gridPane.add(addTabBtn, 0, 0);
//            gridPane.add(delTabBtn, 1, 0);
            gridPane.add(autoConfigBtn, 0, 0);
            gridPane.add(returnBtn, 1, 0);
            gridPane.setAlignment(Pos.CENTER);
//            //设置垂直间距
//            gridPane.setVgap(10);
            //设置水平间距
            gridPane.setHgap(20);
            anchorPane.setBottomAnchor(gridPane, 20.0);
//            gridPane.getChildren().addAll(addTabBtn, delTabBtn, autoConfigBtn, returnBtn);
            vBox.getChildren().addAll(tabPane, gridPane);
            anchorPane.getChildren().add(vBox);

            Stage stage = new Stage();
            stage.setTitle("自动测试");
            stage.setScene(new Scene(anchorPane));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void autoStartBtnOnClick(ActionEvent event) {

        if (innerClientsArrayListForAutoTest.size() <= 0) {
            return;
        }

        if (tcpServer == null || readMessageFromClientService == null || heartBeatService == null) {
            return;
        }

        if (!(tcpServer.isRunning() || readMessageFromClientService.isRunning() || heartBeatService.isRunning())) {
//            powerConnectedBtnOnClick(event);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("设备开启监听服务");
            alert.setHeaderText("");
            alert.setContentText("连接服务尚未打开");
            final Optional<ButtonType> opt = alert.showAndWait();
            return;
        }

        for (List<InnerClient> innerClientList : innerClientsArrayListForAutoTest) {
            if (innerClientList.size() <= 0) {
                continue;
            }

            ControlMessageSendService controlMessageSendService = new ControlMessageSendService();
            controlMessageSendService.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (innerClientList.size() <= 0) {
                        controlMessageSendService.cancel();
                        return;
                    }
//                    logger.info(controlMessageSendService.getTitle() + ": oldValue ==>> " + newValue);
                    InnerClient ic = innerClientList.get(0);
                    if (newValue.intValue() != ic.getGap()) {
                        return;
                    }
                    innerClientList.remove(ic);
                    Client client = new Client();
                    client.setId(ic.getId());
                    client.setName(ic.getName());
                    client.setIp(ic.getIp());
                    client.setVoltage(ic.getVoltage());
                    client.setCurrent(ic.getCurrent());
                    client.setOperateModel(ic.getOperateModel());
                    ServerMessage sm = new ServerMessage();
                    sm.generateControlMessage(client);
                    sm.setControl(new StringBuilder(String.format("%02X", Control.getWorkingStatusByStatus(ic.getControlled()).getCode())));
                    try {
                        try {
                            tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().write(sm.toString().getBytes());
                            tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().flush();
                            if (fileInBlockingQueue.size() >= BUFF_SIZE) {
                                fileInBlockingQueue.take();
                                fileInBlockingQueue.take();
                            }

                            fileInBlockingQueue.put(sm.toString().getBytes());
                            fileInBlockingQueue.put(sm.toString().getBytes());
//                        updateStatusGroup(sm);
                            operateModelListForClientControlled.set(ic.getId(), ic.getOperateModel());
                        } catch (NullPointerException ne) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("未找到连接的设备");
                            alert.setHeaderText("");
                            alert.setContentText(client.getName() + "连接服务尚未打开");
                            final Optional<ButtonType> opt = alert.showAndWait();
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            controlMessageSendService.setPeriod(Duration.seconds(1));
            controlMessageSendService.setDelay(Duration.millis(50));
            controlMessageSendService.start();
            logger.info(Thread.currentThread() + " timeScheduledService init!");
        }
    }


    private class ControlMessageSendService extends ScheduledService<Number> {

        @Override
        protected Task<Number> createTask() {

            Task<Number> task = new Task<Number>() {
                int timer_1S = 0;

                @Override
                protected Number call() throws Exception {
                    this.updateTitle(Thread.currentThread().getName());
                    while (!isCancelled()) {
                        Thread.sleep(1000);
                        this.updateValue(timer_1S++);
                    }
                    return null;
                }
            };

            return task;
        }
    }

//    private int timeCounter = 0;

//    private class TimeScheduledService extends ScheduledService<Number> {
//
//        List<InnerClient> innerClientList;
//
//        public List<InnerClient> getInnerClientList() {
//            return innerClientList;
//        }
//
//        public void setInnerClientList(List<InnerClient> innerClientList) {
//            this.innerClientList = innerClientList;
//        }
//        @Override
//        protected Task<Number> createTask() {
//
//            Task<Number> task = new Task<Number>() {
//                @Override
//                protected void updateValue(Number value) {
//                    super.updateValue(value);
//                    logger.info(Thread.currentThread() +  " value  ==>> "+value);
//                    InnerClient ic = innerClientList.get(0);
//                    if (value.intValue() != ic.getGap()) {
//                        return;
//                    }
//                    innerClientList.remove(ic);
//                    Client client = new Client();
//                    client.setId(ic.getId());
//                    client.setName(ic.getName());
//                    client.setIp(ic.getIp());
//                    client.setVoltage(ic.getVoltage());
//                    client.setCurrent(ic.getCurrent());
//                    client.setOperateModel(ic.getOperateModel());
//                    ServerMessage sm = new ServerMessage();
//                    sm.generateControlMessage(client);
//                    sm.setControl(new StringBuilder(String.format("%02X", Control.getWorkingStatusByStatus(ic.getControlled()).getCode())));
//                    try {
//                        try {
//                            tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().write(sm.toString().getBytes());
//                            tcpServer.getSocketMap().get(StringUtils.hexStr2Ip(sm.getClientIp().toString())).getOutputStream().flush();
//                            if (fileInBlockingQueue.size() >= BUFF_SIZE) {
//                                fileInBlockingQueue.take();
//                                fileInBlockingQueue.take();
//                            }
//
//                            fileInBlockingQueue.put(sm.toString().getBytes());
//                            fileInBlockingQueue.put(sm.toString().getBytes());
////                        updateStatusGroup(sm);
//                            operateModelListForClientControlled.set(ic.getId(), ic.getOperateModel());
//                        } catch (NullPointerException ne) {
//                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                            alert.setTitle("未找到连接的设备");
//                            alert.setHeaderText("");
//                            alert.setContentText(client.getName() + "连接服务尚未打开");
//                            final Optional<ButtonType> opt = alert.showAndWait();
//                        }
//                    } catch (InterruptedException | IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (innerClientList.size() <= 0) {
//                        this.cancel();
//                    }
//                }
//
//                @Override
//                protected Number call() throws Exception {
////                    timeCounter++;
//                    logger.info(Thread.currentThread() + " timeCounter ==>" + timeCounter);
//                    return timeCounter++;
//                }
//            };
//            return task;
//        }
//    }
}




