package com.longriver.kejiapower.controllers;

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
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
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
    void portTextFieldOnClick(ActionEvent event) {
    }

    private static final int BUFF_SIZE = 1024;
    private static final float Base = 10.0f;
    private static final float KILO = 1000.0f;
    private static final int CLIENT_AMOUNT = 8;

    public static Logger logger = LoggerFactory.getLogger(KejiaPowerController.class);

    private BlockingQueue<String> inBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    private BlockingQueue<String> outBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);

    private BlockingQueue<byte[]> fileInBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    //    private BlockingQueue<byte[]> fileOutBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);
    private BlockingQueue<Message> repaintBlockingQueue = new ArrayBlockingQueue<>(BUFF_SIZE);

//    ExecutorService es = Executors.newFixedThreadPool(1);

    private TcpServer tcpServer = new TcpServer(inBlockingQueue, outBlockingQueue);//
    private ReadMessageFromClientService readMessageFromClientService = new ReadMessageFromClientService();
    private String firstStringOfInBlockingQueue = null;//

    private Map<String, XYChart.Series> clinetSeriesMap = new HashMap<>();

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

    private ObservableList<Client> clientObservableList = FXCollections.observableArrayList();
    private Map<String, Client> clientMap = new HashMap<>(CLIENT_AMOUNT);//根据Client的 IP，放入，加速遍历更新clientObservableList

    @FXML
    private void initClientTable() {

        //绑定数据到TableView
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        powerTableView.setItems(clientObservableList);

        Client client = new Client();
        client.setId(clientObservableList.size() + 1);
        client.setIp("127.0.0.1:15195");
        client.setStatus(WorkingStatus.getWorkingStatusByCode((short) 0x08));
        clientObservableList.add(client);
        client = new Client();
        client.setId(clientObservableList.size() + 1);
        client.setIp("127.0.0.1:15196");
        client.setStatus(WorkingStatus.getWorkingStatusByCode((short) 0x10));
        clientObservableList.add(client);
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
                        break;
                    case Report:
                        updateClient(clientMessage);
                        //获取tab里控件，进行画图
                        AnchorPane anchorPane = (AnchorPane) powerDisplayTab.getSelectionModel().getSelectedItem().getContent();
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
                        updateClientList(clientMessage);
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

    /*
     * 根据采样的信号message，更新tab和table里的组件
     * 包括：tab的曲线图，table里的设备列表
     * 需要：同时更新clientObservableList和clientMap
     * */
    private void updateClientList(ClientMessage clientMessage) {
        //更新ClientTable/clientMap
        if (clientMap.get(clientMessage.getClientIp().toString()) == null) {
            Client ct = new Client();
            ct.setId(clientObservableList.size() + 1);
            ct.setName((new  StringBuilder("电源-").append(clientObservableList.size() + 1)).toString());
            ct.setIp(StringUtils.hexStr2Ip(clientMessage.getClientIp().toString()));
            ct.setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 10)));
            clientMap.put(clientMessage.getClientIp().toString(), ct);
            clientObservableList.add(ct);
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
        } else {
            Client client = clientMap.get(clientMessage.getClientIp().toString());
            client.setVoltage(Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base);
            client.setCurrent(Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base);
            client.setPower(Integer.valueOf(clientMessage.getVoltage().toString(), 16).floatValue() / Base * Integer.valueOf(clientMessage.getCurrent().toString(), 16).floatValue() / Base / KILO);
            client.setOperateModel(OperateModel.getOperateModelByCode(Short.parseShort(clientMessage.getModel().toString(), 10)));
            client.setStatus(WorkingStatus.getWorkingStatusByCode(Short.parseShort(clientMessage.getStatus().toString(), 10)));
            clientMap.replace(clientMessage.getClientIp().toString(),client);
            clientObservableList.remove(client.getId());
            clientObservableList.add(client.getId(),client);
        }
    }


    @FXML
    private void initialize() {
        initChart();
        initInteractMessage();
        initClientTable();
        powerTabGenerate();
        powerTabGenerate();
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

//        int clientThread = powerDisplayTab.getTabs().size();
        try {
            switch (powerConnectedBtn.getText()) {
                case "连接设备":
                    tcpServer.setPORT(Integer.parseInt(portTextField.getText(), 10));
                    tcpServer.start();
                    readMessageFromClientService.start();
                    rxTextBtn.setDisable(false);
                    txTextBtn.setDisable(false);
                    logger.info("TcpServer start!");
                    logger.info("powerConnectedBtnOnClick!");
                    powerConnectedBtn.setText("断开设备");
                    break;
                case "断开设备":
                    tcpServer.cancel();
                    tcpServer.reset();
                    readMessageFromClientService.cancel();
                    readMessageFromClientService.reset();
                    rxTextBtn.setDisable(true);
                    txTextBtn.setDisable(true);
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


        if (!(txTextArea.getText() == null || txTextArea.getText().length() <= 0) && null != tcpServer.getSocketMap().get(clientMessage.getClientIp().toString())) {
            try {
//                outBlockingQueue.put(txTextArea.getText().replaceAll(" +", ""));
//                tcpServer.getSocket().getOutputStream().write(outBlockingQueue.take().replaceAll(" +", "").getBytes());
                tcpServer.getSocketMap().get(clientMessage.getClientIp().toString()).getOutputStream().write(txTextArea.getText().replaceAll(" +", "").getBytes());
                tcpServer.getSocketMap().get(clientMessage.getClientIp().toString()).getOutputStream().flush();
//                tcpServer.Send(outBlockingQueue);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
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
    }
}




