package main.java.com.longriver.kejiapower;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.com.longriver.kejiapower.controllers.KejiaPowerController;

import java.net.URL;

public class Main extends Application {

//    private BlockingQueue<String> inBlockingQueue = new ArrayBlockingQueue<>(1024);
//    private BlockingQueue<String> outBlockingQueue = new ArrayBlockingQueue<>(1024);

    @Override
    public void start(Stage primaryStage) throws Exception {

//        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("main/java/com/longriver/kejiapower/view/fxml/kejiapowerController.fxml"));

        URL url = getClass().getClassLoader().getResource("main/java/com/longriver/kejiapower/view/fxml/kejiapowerController.fxml");
//        Parent root = FXMLLoader.load(url);
        KejiaPowerController kejiaPowerController = new KejiaPowerController();

//        kejiaPowerController.setInBlockingQueue(inBlockingQueue);
//        kejiaPowerController.setOutBlockingQueue(outBlockingQueue);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(kejiaPowerController);
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("main/java/com/longriver/kejiapower/view/fxml/kejiapowerController.fxml"));
//        fxmlLoader.load();
        Parent root = fxmlLoader.load();

//        KejiaPowerController kejiaPowerController = fxmlLoader.getController();


        primaryStage.setTitle("科加电源");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();


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
        launch(args);
    }
}
