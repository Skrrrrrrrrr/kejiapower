package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.POJO.ClientMessage;
import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.model.InnerClient;
import com.longriver.kejiapower.utils.OperateModel;
import com.longriver.kejiapower.utils.WorkingStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    private ObservableList<InnerClient> innerClientObservableList = FXCollections.observableArrayList();

//    public FastPowerConfigController(ObservableList<Client> clientObservableList) {
//        this.clientObservableList = clientObservableList;
//    }


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
        TableColumn<InnerClient, Boolean> selectiveColumn = new TableColumn<>("勾选");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        voltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));
        currentColumn.setCellValueFactory(new PropertyValueFactory<>("current"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("operateModel"));
        selectiveColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectiveColumn.setCellFactory((TableColumn<InnerClient, Boolean> tc) -> {
            return new CheckBoxTableCell<>();
        });

        ipColumn.setCellFactory(new Callback<TableColumn<InnerClient, String>, TableCell<InnerClient, String>>() {
            @Override
            public TableCell<InnerClient, String> call(TableColumn<InnerClient, String> param) {
                return new TextFieldTableCell<>();
            }
        });

        voltageColumn.setCellFactory(new Callback<TableColumn<InnerClient, Float>, TableCell<InnerClient, Float>>() {
            @Override
            public TableCell<InnerClient, Float> call(TableColumn<InnerClient, Float> param) {
                return new TextFieldTableCell<>();
            }
        });

        currentColumn.setCellFactory(new Callback<TableColumn<InnerClient, Float>, TableCell<InnerClient, Float>>() {
            @Override
            public TableCell<InnerClient, Float> call(TableColumn<InnerClient, Float> param) {
                return new TextFieldTableCell<>();
            }
        });

        columns.addAll(idColumn, nameColumn, ipColumn, voltageColumn, currentColumn, statusColumn, modelColumn, selectiveColumn);

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
//        innerClientObservableList = InnerClient.getInnerClassObservableList(clientObservableList);

    }

    private void updateInnerClientObservableList(ObservableList<Client> clientObservableList) {
        setClientObservableList(clientObservableList);
    }

    //TODO
    private ObservableList<ClientMessage> getObservableListFromTableView() {
        ObservableList<ClientMessage> clientMessageObservableList;

        return null;
    }

    public void setInnerClassObservableList(ObservableList<Client> clientObservableList) {
//        innerClientObservableList = FXCollections.observableArrayList();
//        this.clientObservableList = clientObservableList;
        if (clientObservableList != null && clientObservableList.size() > 0) {
            for (Client ct : clientObservableList) {
                InnerClient innerClient = new InnerClient();
                innerClient.setId(ct.getId());
                innerClient.setIp(ct.getIp());
                innerClient.setName(ct.getName());
                innerClient.setVoltage(ct.getVoltage());
                innerClient.setCurrent(ct.getCurrent());
                innerClient.setOperateModel(ct.getOperateModel());
                innerClient.setStatus(ct.getStatus());
                innerClient.setSelected(true);

                innerClientObservableList.add(innerClient);
            }
        }
    }

    public ObservableList<Client> getInnerClassObservableList() {

        return clientSetUpObservableList;
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
        innerClient.setId(innerClientObservableList.get(innerClientObservableList.size() - 1).getId() + 1);
//        innerClient.setId(innerClientObservableList.get(innerClientObservableList.size()).getId() + 1);
        innerClient.setName(new StringBuilder("电源-" + innerClient.getId()).toString());
        innerClient.setStatus(WorkingStatus.UNKNOWN);
        innerClient.setSelected(false);
        innerClientObservableList.add(innerClient);
    }

    private ObservableList<Client> clientSetUpObservableList = FXCollections.observableArrayList();

    @FXML
    public void fastSetUpBtnOnClick(ActionEvent event) {

        for (InnerClient ic : innerClientObservableList) {
            if (ic.isSelected()) {
                Client client = new Client();
                client.setId(ic.getId());
                client.setName(ic.getName());
                client.setIp(ic.getIp());
                client.setVoltage(ic.getVoltage());
                client.setCurrent(ic.getCurrent());
                client.setOperateModel(ic.getOperateModel());
                client.setTime(ic.getTime());
                clientSetUpObservableList.add(client);
            }
        }
    }

}
