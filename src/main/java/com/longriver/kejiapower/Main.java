package com.longriver.kejiapower;

import com.longriver.kejiapower.controllers.KejiaPowerController;
import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.StringUtils;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.Optional;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

//        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main/java/com/longriver/kejiapower/view/fxml/kejiapowerController.fxml"));

        URL url = getClass().getClassLoader().getResource("view/fxml/kejiapowerController.fxml");
//        Parent root = FXMLLoader.load(url);
        KejiaPowerController kejiaPowerController = new KejiaPowerController();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(kejiaPowerController);
        fxmlLoader.setLocation(url);
//        fxmlLoader.load();
        Parent root = fxmlLoader.load();

//        KejiaPowerController kejiaPowerController = fxmlLoader.getController();

        primaryStage.setTitle("科加电源");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("退出程序");
                alert.setHeaderText("");
                alert.setContentText("您真的要退出吗？");
                final Optional<ButtonType> opt = alert.showAndWait();
                final ButtonType rtn = opt.get();
                if (rtn == ButtonType.CANCEL) {
                    event.consume();
                }
            }
        });

//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    if (!(tcpServer.getOutputString() == null || tcpServer.getOutputString().length() <= 0)) {
//
//                    }
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                }
//            }
//        });

    }


    public static void main(String[] args) {
//        DataFrame.getClientIP("FFFF0BCCDD04FFF1DD01DD");
//        StringUtils.ip2HexStr("127.0.0.1");
        launch(args);
    }
}
