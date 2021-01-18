package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.POJO.ClientMessage;
import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.model.InnerClient;
import com.longriver.kejiapower.utils.Control;
import com.longriver.kejiapower.utils.OperateModel;
import com.longriver.kejiapower.utils.WorkingStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.FloatStringConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FastPowerConfigController {

    @FXML
    private TableView<InnerClient> fastConfigTableView;

//    @FXML
//    private TableColumn<InnerClient, Integer> idColumn;
//
//    @FXML
//    private TableColumn<InnerClient, String> ipColumn;
//
//    @FXML
//    private TableColumn<InnerClient, Float> voltageColumn;
//
//    @FXML
//    private TableColumn<InnerClient, Float> currentColumn;
//
//    @FXML
//    private TableColumn<InnerClient, Short> controlColumn;
//
//    @FXML
//    private TableColumn<InnerClient, OperateModel> modelColumn;
//
//    @FXML
//    private TableColumn<InnerClient, Boolean> selectiveColumn;

    @FXML
    private Button fastSetUpBtn;

    @FXML
    private Button returnBtn;

    private final String IPREG = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
    private static final int CLIENT_AMOUNT = 8;


    @FXML
    void returnBtnOnClick(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

        //        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("退出程序");
//        alert.setHeaderText("");
//        alert.setContentText("您真的要退出吗？");
//        final Optional<ButtonType> opt = alert.showAndWait();
//        final ButtonType rtn = opt.get();
//        if (rtn == ButtonType.CANCEL) {
//            event.consume();
//        } else {
//            Node source = (Node) event.getSource();
//            Stage stage = (Stage) source.getScene().getWindow();
//            stage.close();
//        }
    }

    private ObservableList<Client> clientObservableList = FXCollections.observableArrayList();
    private Map<Client, Control> clientControlMap = new HashMap<>(CLIENT_AMOUNT);
    private ObservableList<InnerClient> innerClientObservableList = FXCollections.observableArrayList();


    public ObservableList<Client> getClientObservableList() {
        return clientObservableList;
    }

    public void setClientObservableList(ObservableList<Client> clientObservableList) {
        this.clientObservableList = clientObservableList;

    }

    @FXML
    private void initialize() {
        //绑定数据到TableView
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
//        voltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));
//        currentColumn.setCellValueFactory(new PropertyValueFactory<>("current"));
//        controlColumn.setCellValueFactory(new PropertyValueFactory<>("control"));
//        modelColumn.setCellValueFactory(new PropertyValueFactory<>("operateModel"));
//        selectiveColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
//        selectiveColumn.setCellFactory( tc -> new CheckBoxTableCell<>());
////        selectiveColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectiveColumn));
//        fastConfigTableView.setItems(InnerClient.getInnerClassObservableList(clientObservableList));
//
//        fastConfigTableView.setEditable(true);

        final ObservableList<TableColumn<InnerClient, ?>> columns = fastConfigTableView.getColumns();

        TableColumn<InnerClient, Integer> idColumn = new TableColumn<>("序号");
        TableColumn<InnerClient, String> nameColumn = new TableColumn<>("名称");
        TableColumn<InnerClient, String> ipColumn = new TableColumn<>("地址");
        TableColumn<InnerClient, Float> voltageColumn = new TableColumn<>("电压");
        TableColumn<InnerClient, Float> currentColumn = new TableColumn<>("电流");
        TableColumn<InnerClient, WorkingStatus> statusColumn = new TableColumn<>("状态");
        TableColumn<InnerClient, OperateModel> modelColumn = new TableColumn<>("模式");
        TableColumn<InnerClient, Control> controlColumn = new TableColumn<>("控制");
        TableColumn<InnerClient, Boolean> selectiveColumn = new TableColumn<>("勾选");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        voltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));
        currentColumn.setCellValueFactory(new PropertyValueFactory<>("current"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("operateModel"));
        controlColumn.setCellValueFactory(new PropertyValueFactory<>("controlled"));
        selectiveColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));

        ObservableList<Control> controlColumnObservableList = FXCollections.observableArrayList();
        for (Control c : Control.values()) {
            controlColumnObservableList.add(c);

        }
//        controlColumnObservableList.add(Control.INVALID);
//        controlColumnObservableList.add(Control.TERMINATE);
//        controlColumnObservableList.add(Control.INITIATE);
        ObservableList<OperateModel> modelColumnObservableList = FXCollections.observableArrayList();
        for (OperateModel operateModel : OperateModel.values()) {
            modelColumnObservableList.add(operateModel);

        }
//        modelColumnObservableList.add(OperateModel.INVALID);
//        modelColumnObservableList.add(OperateModel.LOCAL);
//        modelColumnObservableList.add(OperateModel.DAEMON50A);
//        modelColumnObservableList.add(OperateModel.DAEMON35A);
//        modelColumnObservableList.add(OperateModel.DAEMON15A);
//        modelColumnObservableList.add(OperateModel.DAEMON15A);

        ComboBox controlComboBox = new ComboBox(controlColumnObservableList);
        controlColumn.setCellFactory(ComboBoxTableCell.forTableColumn(controlColumnObservableList));
        ComboBox modelComboBox = new ComboBox(controlColumnObservableList);
        modelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(modelColumnObservableList));

        selectiveColumn.setCellFactory((TableColumn<InnerClient, Boolean> tc) -> {
            return new CheckBoxTableCell<>();
        });

        ipColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        ipColumn.setCellFactory(new Callback<TableColumn<InnerClient, String>, TableCell<InnerClient, String>>() {
//            @Override
//            public TableCell<InnerClient, String> call(TableColumn<InnerClient, String> param) {
//                return new TextFieldTableCell<>();
//            }
//        });

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

        columns.addAll(idColumn, nameColumn, ipColumn, voltageColumn, currentColumn, statusColumn, modelColumn, controlColumn, selectiveColumn);

        fastConfigTableView.setItems(innerClientObservableList);

//        idColumn.setCellFactory(col->{
//            TableCell<InnerClient, Integer> cell = new TableCell<InnerClient, Integer>(){
//                @Override
//                protected void updateItem(Integer item, boolean empty) {
//                    super.updateItem(item, empty);
//                    this.setText(null);
//                    this.setGraphic(null);
//                }
//            };
//            return cell;
//        });

//        idColumn.setCellFactory(TextFieldTableCell.<String>forTableColumn());
//        fastConfigTableView.setItems(innerClientObservableList);
        fastConfigTableView.setEditable(true);
        fastConfigTableView.setPlaceholder(new Label(""));
//        innerClientObservableList = InnerClient.getInnerClassObservableList(clientObservableList);
        //表格可以多选
//        fastConfigTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

//        fastConfigTableView.getSelectionModel().getSelectedCells().addListener(new InvalidationListener() {
//            @Override
//            public void invalidated(Observable observable) {
//                ObservableList<TablePosition> observableList = (ObservableList<TablePosition>) observable;
//                for(int i=0;i<observableList.size();i++){
//                    TablePosition tablePosition = observableList.get(i);
//                    Object cellData = tablePosition.getTableColumn().getCellData(tablePosition.getRow());
//
//                    System.out.println("您选择的坐标为:("+tablePosition.getColumn()+","+tablePosition.getRow()+")_内容为:"+cellData.toString());
//                }
//            }
//        });

        // 提交事件
        ipColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InnerClient, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<InnerClient, String> event) {
                if (!event.getNewValue().matches(IPREG)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("地址非法");
                    alert.setHeaderText("");
                    alert.setContentText("录入的地址不正确!");
                    final Optional<ButtonType> opt = alert.showAndWait();
                    return;
                }
                innerClientObservableList.get(event.getTablePosition().getRow()).setIp(event.getNewValue());
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
//                    fastConfigTableView.getItems().get(event.getTablePosition().getRow()).setVoltage(0f);
                    return;
                }
//                Object cellData = event.getTablePosition().getTableColumn().getCellData(event.getTablePosition().getRow());
//                fastConfigTableView.getSelectionModel().getSelectedCells().get(event.getTablePosition().getRow());
//                fastConfigTableView.getItems().get(event.getTablePosition().getRow());
                innerClientObservableList.get(event.getTablePosition().getRow()).setVoltage(event.getNewValue());

//                String data = (String) fastConfigTableView.getSelectionModel().getSelectedCells().get(0).getTableColumn().getCellObservableValue(fastConfigTableView.getItems().get(fastConfigTableView.getSelectionModel().getSelectedCells().get(0).getRow())).getValue();


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
                    fastConfigTableView.getItems().get(event.getTablePosition().getRow()).setCurrent(0f);

                    return;
                }
                innerClientObservableList.get(event.getTablePosition().getRow()).setCurrent(event.getNewValue());

            } catch (NumberFormatException ne) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("电流值错误");
                alert.setHeaderText("");
                alert.setContentText("请输入正确数值!");
                final Optional<ButtonType> opt = alert.showAndWait();
            }

        });
        //失去焦点事件
//        textField.focusedProperty().addListener(new ChangeListener<Boolean>()
//        {
//            @Override public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
//            {
//                if ( oldValue && (!newValue) && (!enterkey))
//                {
//                    if ( ToolFunc.isInteger(textField.getText() ))
//                    {
//                        Integer value = Integer.parseInt(textField.getText());
//                        HarmonicPara hp = (HarmonicPara) getTableView().getItems().get(getIndex());
//                        hp.setPercent(value);
//                    }
//                }
//            }
//        });

        controlColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<InnerClient, Control>>() {
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

    //TODO
    private ObservableList<ClientMessage> getObservableListFromTableView() {
        ObservableList<ClientMessage> clientMessageObservableList;

        return null;
    }

    public void setInnerClassObservableList(Map<Client, Control> clientControlMap) {
        if (clientControlMap != null && clientControlMap.size() > 0) {
            for (Client ct : clientControlMap.keySet()) {
                InnerClient innerClient = new InnerClient();
                innerClient.setId(ct.getId());
                innerClient.setIp(ct.getIp());
                innerClient.setName(ct.getName());
                innerClient.setVoltage(ct.getVoltage());
                innerClient.setCurrent(ct.getCurrent());
                innerClient.setOperateModel(ct.getOperateModel());
                innerClient.setStatus(ct.getStatus());
                innerClient.setControlled(Control.TERMINATE.toString());//设置控制方式的默认值：停止
                innerClient.setSelected(true);

                innerClientObservableList.add(innerClient);
            }
        }
    }

    public HashMap<Client, Control> getClientControlMap() {

        return (HashMap<Client, Control>) clientControlMap;
    }

    @FXML
//    void deleteBtnOnClick(TableColumn.CellEditEvent<InnerClient,String> value) {
    void deleteBtnOnClick(ActionEvent event) {
        int selectedIndex = fastConfigTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            innerClientObservableList.remove(selectedIndex);
        }

//            TableColumn<InnerClient, String> tableColumn = value.getTableColumn();//获取了正在编辑的列命名为tc
//        InnerClient cell = value.getRowValue();//获取了正在编辑的行
//        System.out.println(tableColumn.getCellData(cell));//打印了正在编辑的单元格的值旧值
//        System.out.println(value.getNewValue());//
    }

    @FXML
    void increaseBtnOnClick(ActionEvent event) {
        InnerClient innerClient = new InnerClient();
        if (innerClientObservableList.size() <= 0) {
            innerClient.setId(1);
        } else {
            innerClient.setId(innerClientObservableList.get(innerClientObservableList.size() - 1).getId() + 1);
        }
//        innerClient.setId(innerClientObservableList.get(innerClientObservableList.size()).getId() + 1);
        innerClient.setName(new StringBuilder("电源-" + innerClient.getId()).toString());
        innerClient.setStatus(WorkingStatus.UNKNOWN);
        innerClient.setOperateModel(OperateModel.INVALID);
        innerClient.setControlled(Control.INVALID.toString());
        innerClient.setSelected(false);
        innerClientObservableList.add(innerClient);
    }

    private ObservableList<Client> clientSetUpObservableList = FXCollections.observableArrayList();

    @FXML
    public void fastSetUpBtnOnClick(ActionEvent event) {
        if (innerClientObservableList.size() > CLIENT_AMOUNT) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("信息");
            alert.setHeaderText("设置的电源数量不能大于8！");
            final Optional<ButtonType> opt = alert.showAndWait();
        }
        if (innerClientObservableList.size() <= 0) {
            return;
        }
        try {
            for (InnerClient ic : innerClientObservableList) {
                if (ic.getOperateModel() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText("第 " + ic.getId() + "行" + "电源 模式 参数尚未设置");
                    final Optional<ButtonType> opt = alert.showAndWait();
                    return;
                }
                if (ic.getControlled() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText("第 " + ic.getId() + "行" + "电源 控制 参数尚未设置");
                    final Optional<ButtonType> opt = alert.showAndWait();
                    return;
                }
                if (ic.getIp() == null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("提示");
                    alert.setHeaderText("第 " + ic.getId() + "行" + "电源 Ip 参数尚未设置");
                    final Optional<ButtonType> opt = alert.showAndWait();
                    return;
                }
                if (ic.isSelected()) {
                    Client client = new Client();
                    client.setId(ic.getId());
                    client.setName(ic.getName());
                    if (ic.getIp() != null || ic.getIp().matches(IPREG)) {
                        client.setIp(ic.getIp());
//                    innerClientObservableList.get(ic.getId()-1).setIp(ic.getIp());
                    } else {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("地址错误");
                        alert.setHeaderText("");
                        alert.setContentText("第 " + ic.getId() + "行" + "录入的地址不正确!");
                        final Optional<ButtonType> opt = alert.showAndWait();
                        return;
                    }
                    client.setVoltage(ic.getVoltage());
                    client.setCurrent(ic.getCurrent());
                    client.setOperateModel(ic.getOperateModel());
                    client.setTime(ic.getTime());
                    if (clientControlMap.size() <= 0 || !clientControlMap.keySet().contains(client)) {
                        clientControlMap.put(client, Control.getWorkingStatusByStatus(ic.getControlled()));

                    } else {
                        clientControlMap.replace(client, Control.getWorkingStatusByStatus(ic.getControlled()));
                    }
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("成功");
            alert.setHeaderText("参数设置成功！");
            final Optional<ButtonType> opt = alert.showAndWait();
        } catch (Exception e) {

        }
    }

}
