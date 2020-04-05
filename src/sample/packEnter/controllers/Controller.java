package sample.packEnter.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.DataClient;
import sample.connection.GetData;
import sample.packEnter.NavigatorEnter;
import sample.packFileManager.DataFile;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button signIn;

    @FXML
    private Button signUp;

    @FXML
    private AnchorPane Holder;

    @FXML
    private Label label;

    @FXML
    void signInClicked(ActionEvent event) throws InterruptedException, IOException {

        if (loginText.getText().equals(""))
        {
            loginText.setPromptText("Заполните поле");
        }
        else if (passwordText.getText().equals(""))
        {
            passwordText.setPromptText("Заполните поле");
        }
        else
        {
            DataClient.login = loginText.getText();
            DataClient.password = passwordText.getText();
            try {
                if (GetData.getDataMessage("AUTHORIZATION / ://101") == 100)
                {
                    Parent root;
                    try {
                        root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/packFileManager/scenepack/FileManager.fxml"), resources);
                        Stage stage = new Stage();
                        stage.setTitle("File Manager");
                        stage.setScene(new Scene(root, 1280, 720));
                        ((Node) (event.getSource())).getScene().getWindow().hide();
                        stage.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    label.setText("Неправильный логин или пароль");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }




    }

    @FXML
    void signUpClicked(ActionEvent event) {
        NavigatorEnter.loadVista(NavigatorEnter.SIGN_UP);
    }

    @FXML
    void initialize() {


    }

    public void setVista(Node node) {
        Holder.getChildren().setAll(node);
    }


}



