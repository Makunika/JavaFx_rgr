package sample.packEnter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.packEnter.NavigatorEnter;
import sample.packEnter.controllers.Controller;

import java.io.IOException;

public class Main extends Application {

  /*  @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("scenepack/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }*/

    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("File Manager");

        stage.setScene(createScene(loadMainPane()));

        stage.show();
    }


    private Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane mainPane = (Pane) loader.load(getClass().getResourceAsStream(NavigatorEnter.ENTER));

        Controller mainController = loader.getController();

        NavigatorEnter.setMainController(mainController);

        return mainPane;
    }

    private Scene createScene(Pane mainPane) {
        Scene scene = new Scene(mainPane);
        return scene;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
