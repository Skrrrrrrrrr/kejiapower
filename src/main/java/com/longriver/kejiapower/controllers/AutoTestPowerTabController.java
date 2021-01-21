package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.model.InnerClient;
import com.longriver.kejiapower.utils.Control;
import com.longriver.kejiapower.utils.OperateModel;
import com.longriver.kejiapower.utils.WorkingStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

import javax.print.attribute.standard.NumberUp;
import java.util.*;

public class AutoTestPowerTabController {
    @FXML
    private LineChart<Number, Number> setUpLineChart;

    @FXML
    private TableView<InnerClient> dataTableView;

    @FXML
    private Label idLabel;

    @FXML
    private Label ipLable;

    @FXML
    private Button addLineBtn = new Button();

    @FXML
    private Button delLineBtn = new Button();

    @FXML
    private Button setUpBtn = new Button();

    @FXML
    private Button autoConfigBtn = new Button();

    @FXML
    private Button returnBtn = new Button();

    @FXML
    private Button addTabBtn = new Button();

    @FXML
    private Button delTabBtn = new Button();

    private final String IPREG = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
    private static final int CLIENT_AMOUNT = 8;
    private static final int BUFF_SIZE = 1024;

    private Client client;

    private ObservableList<InnerClient> innerClientObservableList = FXCollections.observableArrayList();
    private Map<Client, Control> clientControlMap = new HashMap<>(CLIENT_AMOUNT);
    private List<InnerClient> innerClientArrayListForAutoTest = new ArrayList<>(BUFF_SIZE);//自动测试数据：认为状态不同的同一地址Client为不同的Client实体

    private XYChart.Series voltageSeries = new XYChart.Series();

    @FXML
    private void initialize() {
        lineChartInit();
        dataTableViewInit();
    }

    private void lineChartInit() {
        NumberAxis x = new NumberAxis(0, 120, 20);
        NumberAxis y = new NumberAxis(0, 1000, 100);
        x.setAutoRanging(true);
        y.setAutoRanging(true);
        voltageSeries.setName("Voltage(V)");
//        pChart.setTranslateY(-80);
        setUpLineChart = new LineChart<Number, Number>(x, y);
        setUpLineChart.getData().add(voltageSeries);
        setUpLineChart.setLegendSide(Side.TOP);
//        setUpLineChart.setLegendVisible(true);
        setUpLineChart.setCreateSymbols(false);
        ((GridPane) dataTableView.getParent()).add(setUpLineChart, 0, 0);

    }

    private void dataTableViewInit() {
        final ObservableList<TableColumn<InnerClient, ?>> columns = dataTableView.getColumns();

        TableColumn<InnerClient, Integer> gapColumn = new TableColumn<>("时间");
        TableColumn<InnerClient, Float> voltageColumn = new TableColumn<>("电压");
        TableColumn<InnerClient, Float> currentColumn = new TableColumn<>("电流");
        TableColumn<InnerClient, OperateModel> modelColumn = new TableColumn<>("模式");
        TableColumn<InnerClient, Control> controlColumn = new TableColumn<>("控制");

        gapColumn.setCellValueFactory(new PropertyValueFactory<>("gap"));
        voltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));
        currentColumn.setCellValueFactory(new PropertyValueFactory<>("current"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("operateModel"));
        controlColumn.setCellValueFactory(new PropertyValueFactory<>("controlled"));

        ObservableList<Control> controlColumnObservableList = FXCollections.observableArrayList();
        for (com.longriver.kejiapower.utils.Control c : com.longriver.kejiapower.utils.Control.values()) {
            controlColumnObservableList.add(c);
        }
        ObservableList<OperateModel> modelColumnObservableList = FXCollections.observableArrayList();
        for (OperateModel operateModel : OperateModel.values()) {
            modelColumnObservableList.add(operateModel);

        }

        ComboBox controlComboBox = new ComboBox(controlColumnObservableList);
        controlColumn.setCellFactory(ComboBoxTableCell.forTableColumn(controlColumnObservableList));
        ComboBox modelComboBox = new ComboBox(controlColumnObservableList);
        modelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(modelColumnObservableList));

        gapColumn.setCellFactory((TableColumn<InnerClient, Integer> tc) -> {
            return new TextFieldTableCell<>(new IntegerStringConverter());
        });
//        gapColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        voltageColumn.setCellFactory(new Callback<TableColumn<InnerClient, Float>, TableCell<InnerClient, Float>>() {
            @Override
            public TableCell<InnerClient, Float> call(TableColumn<InnerClient, Float> param) {
                return new TextFieldTableCell<>(new FloatStringConverter());
            }
        });

        currentColumn.setCellFactory(new Callback<TableColumn<InnerClient, Float>, TableCell<InnerClient, Float>>() {
            @Override
            public TableCell<InnerClient, Float> call(TableColumn<InnerClient, Float> param) {
                return new TextFieldTableCell<>(new FloatStringConverter());
            }
        });

        columns.addAll(gapColumn, voltageColumn, currentColumn, modelColumn, controlColumn);

        dataTableView.setItems(innerClientObservableList);
        dataTableView.setEditable(true);
        dataTableView.setPlaceholder(new Label(""));
        dataTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 提交事件
        gapColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InnerClient, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<InnerClient, Integer> event) {
                if (!String.valueOf(event.getNewValue()).matches("^[0-9]+$")) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("时间设置错误");
                    alert.setHeaderText("");
                    alert.setContentText("时间间隔应为整数（分钟）！");
                    final Optional<ButtonType> opt = alert.showAndWait();
                    return;
                }
                innerClientObservableList.get(event.getTablePosition().getRow()).setGap(event.getNewValue());

            }
        });

        voltageColumn.setOnEditCommit((TableColumn.CellEditEvent<InnerClient, Float> event) -> {
            try {
                if (event.getNewValue() < 0.0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("电压值错误");
                    alert.setHeaderText("");
                    alert.setContentText("电压值不能为负!");
                    final Optional<ButtonType> opt = alert.showAndWait();
                    return;
                }
                innerClientObservableList.get(event.getTablePosition().getRow()).setVoltage(event.getNewValue());
            } catch (NumberFormatException ne) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("电压值错误");
                alert.setHeaderText("");
                alert.setContentText("请输入正确数值!");
                final Optional<ButtonType> opt = alert.showAndWait();
            }
        });
        currentColumn.setOnEditCommit((TableColumn.CellEditEvent<InnerClient, Float> event) -> {
            try {
                if (event.getNewValue() < 0.0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("电流值错误");
                    alert.setHeaderText("");
                    alert.setContentText("电流值不能为负!");
                    final Optional<ButtonType> opt = alert.showAndWait();
                    return;
                }
                innerClientObservableList.get(event.getTablePosition().getRow()).setVoltage(event.getNewValue());
            } catch (NumberFormatException ne) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("电流值错误");
                alert.setHeaderText("");
                alert.setContentText("请输入正确数值!");
                final Optional<ButtonType> opt = alert.showAndWait();
            }
        });

        controlColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InnerClient, com.longriver.kejiapower.utils.Control>>() {
                                          @Override
                                          public void handle(TableColumn.CellEditEvent<InnerClient, Control> event) {
                                              ((InnerClient) event.getTableView().getItems().get(event.getTablePosition().getRow())).setControlled(event.getNewValue().toString());
                                          }
                                      }


        );
        modelColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InnerClient, OperateModel>>() {
                                        @Override
                                        public void handle(TableColumn.CellEditEvent<InnerClient, OperateModel> event) {
                                            ((InnerClient) event.getTableView().getItems().get(event.getTablePosition().getRow())).setOperateModel(OperateModel.getOperateModelByModel(event.getNewValue().toString()));
                                        }
                                    }


        );
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    public void clientToInnerClientPara(Client client) {
        setClient(client);
        idLabel.setText((new StringBuilder(client.getName() + "： ")).toString());
        ipLable.setText(client.getIp());

        InnerClient innerClient = new InnerClient();
        innerClient.setId(client.getId());
        innerClient.setName(client.getName());
        innerClient.setGap(0);
        innerClient.setIp(client.getIp());
//        innerClient.setStatus(client.getStatus());
        innerClient.setStatus(WorkingStatus.UNKNOWN);
        innerClient.setOperateModel(OperateModel.INVALID);
        innerClient.setControlled(Control.INVALID.toString());
        innerClientObservableList.add(innerClient);

    }

    public List<InnerClient> generateInnerClientArrayListForAutoTest() {

        return innerClientArrayListForAutoTest;
    }

    @FXML
    void addLineBtnOnClick(ActionEvent event) {
        InnerClient innerClient = new InnerClient();
        innerClient.setId(client.getId());
        innerClient.setName(client.getName());
        innerClient.setIp(client.getIp());
        innerClient.setStatus(WorkingStatus.UNKNOWN);
        innerClient.setOperateModel(OperateModel.INVALID);
        innerClient.setControlled(Control.INVALID.toString());
        innerClientObservableList.add(innerClient);
    }


    @FXML
    void delLineBtnOnClick(ActionEvent event) {
        int selectedIndex = dataTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            innerClientObservableList.remove(selectedIndex);
        }
    }

    @FXML
    void setUpBtnOnClick(ActionEvent event) {
        if (innerClientObservableList.size() <= 0) {
            return;
        }
        voltageSeries.getData().clear();
        float voltage = 0;
        voltageSeries.getData().add(new XYChart.Data<Number, Number>(0, voltage));

        for (InnerClient ic : innerClientObservableList) {
            voltageSeries.getData().add(new XYChart.Data<Number, Number>(ic.getGap(), voltage));
            innerClientArrayListForAutoTest.add(ic);
            voltage = ic.getVoltage();
            voltageSeries.getData().add(new XYChart.Data<Number, Number>(ic.getGap(), voltage));
        }
        XYChart.Series tailSeries = new XYChart.Series();
        tailSeries.getData().add(new XYChart.Data<Number, Number>(innerClientObservableList.get(innerClientObservableList.size() - 1).getGap(), innerClientObservableList.get(innerClientObservableList.size() - 1).getVoltage()));
        tailSeries.getData().add(new XYChart.Data<Number, Number>(innerClientObservableList.get(innerClientObservableList.size() - 1).getGap() + 50, innerClientObservableList.get(innerClientObservableList.size() - 1).getVoltage()));
        setUpLineChart.getData().add(tailSeries);
//        ((NumberAxis) setUpLineChart.getXAxis()).getRange();
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                voltageSeries.getData().add(new XYChart.Data<Number, Number>(setUpLineChart.getXAxis().getRange(), voltageSeries.getData().get(-1)));
//            }
//        });

    }
}
