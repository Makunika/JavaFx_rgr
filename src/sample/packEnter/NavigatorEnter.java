package sample.packEnter;

import javafx.fxml.FXMLLoader;
import sample.packEnter.controllers.Controller;

import java.io.IOException;

/**
 * Utility class for controlling navigation between vistas.
 *
 * All methods on the navigator are static to facilitate
 * simple access from anywhere in the application.
 */
public class NavigatorEnter {


    public static final String ENTER   = "scenepack/sample.fxml";
    public static final String SIGN_UP = "scenepack/registration.fxml";

    private static Controller mainController;


    public static void setMainController(Controller mainController) {

        NavigatorEnter.mainController = mainController;
    }


    public static void loadVista(String fxml) {
        try {
            mainController.setVista(FXMLLoader.load(NavigatorEnter.class.getResource(fxml)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
