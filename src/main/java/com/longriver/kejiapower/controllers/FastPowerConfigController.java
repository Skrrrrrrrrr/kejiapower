package com.longriver.kejiapower.controllers;

import com.longriver.kejiapower.POJO.ClientMessage;
import com.longriver.kejiapower.model.Client;
import com.longriver.kejiapower.test.Os;
import com.longriver.kejiapower.utils.OperateModel;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.*;

public class FastPowerConfigController {

    @FXML
    private TableView<Client> fastConfigTableView;

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
    void fastSetUpBtnOnClick(ActionEvent event) {

    }

    @FXML
    void returnBtnOnClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("退出程序");
        alert.setHeaderText("");
        alert.setContentText("您真的要退出吗？");
        final Optional<ButtonType> opt = alert.showAndWait();
        final ButtonType rtn = opt.get();
        if (rtn == ButtonType.CANCEL) {
            event.consume();
        } else {
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
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

        final ObservableList<TableColumn<Client, ?>> columns = fastConfigTableView.getColumns();

        TableColumn<Client, Integer> idColumn = new TableColumn<>("序号");
        TableColumn<Client, String> ipColumn = new TableColumn<>("地址");
        TableColumn<Client, Float> voltageColumn = new TableColumn<>("电压");
        TableColumn<Client, Float> currentColumn = new TableColumn<>("电流");
        TableColumn<Client, Short> controlColumn = new TableColumn<>("控制");
        TableColumn<Client, OperateModel> modelColumn = new TableColumn<>("模式");
        TableColumn<Client, Boolean> selectiveColumn = new TableColumn<>("");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
        voltageColumn.setCellValueFactory(new PropertyValueFactory<>("voltage"));
        currentColumn.setCellValueFactory(new PropertyValueFactory<>("current"));
        controlColumn.setCellValueFactory(new PropertyValueFactory<>("control"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("operateModel"));
        selectiveColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        selectiveColumn.setCellFactory((TableColumn<Client, Boolean> tc) -> {
            return new CheckBoxTableCell<>();
        });
        columns.addAll(idColumn, ipColumn, voltageColumn, currentColumn, controlColumn, modelColumn, selectiveColumn);

        fastConfigTableView.setItems(clientObservableList);
//        fastConfigTableView.setItems(innerClientObservableList);
        fastConfigTableView.setEditable(true);
//        innerClientObservableList = InnerClient.getInnerClassObservableList(clientObservableList);

    }

    public void updateInnerClientObservableList(ObservableList<Client> clientObservableList) {
        setClientObservableList(clientObservableList);
        innerClientObservableList = InnerClient.getInnerClassObservableList(clientObservableList);

    }


    private ObservableList<ClientMessage> getObservableListFromTableView() {
        ObservableList<ClientMessage> clientMessageObservableList;

        return null;
    }

    private static class InnerClient extends Client {
        private BooleanProperty selected = new SimpleBooleanProperty();
        private BooleanProperty control = new SimpleBooleanProperty();


        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        public boolean isControl() {
            return control.get();
        }

        public BooleanProperty controlProperty() {
            return control;
        }

        public void setControl(boolean control) {
            this.control.set(control);
        }

        private static ObservableList<InnerClient> getInnerClassObservableList(ObservableList<Client> clientObservableList) {
            ObservableList<InnerClient> innerClientObservableList = FXCollections.observableArrayList();
            if (clientObservableList != null && clientObservableList.size() > 0) {
                for (Client ct : clientObservableList) {
                    InnerClient innerClient = (InnerClient) ct;
                    innerClient.setSelected(false);
                    innerClientObservableList.add(innerClient);
                }
            }
            return innerClientObservableList;
        }
    }

    @FXML
    void deleteBtnOnClick(ActionEvent event) {

    }

    @FXML
    void increaseBtnOnClick(ActionEvent event) {

    }

}
