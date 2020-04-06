package sample.packEnter.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import sample.DataClient;
import sample.connection.GetData;
import sample.packEnter.NavigatorEnter;

public class Registration {

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane paneReg;

    @FXML
    private URL location;

    @FXML
    private TextField loginText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button signUp;

    @FXML
    private TextField emailText;

    @FXML
    private Button back;

    @FXML
    private Label label;

    @FXML
    void signUpClicked(ActionEvent event) {

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


            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (GetData.getDataMessage("REGISTRATION / ://100") == 100)
                        {
                            loadBack();
                        }
                        else
                        {
                            label.setText("Такой пользователь уже существует");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });



        }

    }

    @FXML
    void backClicked(ActionEvent event) {
        NavigatorEnter.loadVista(NavigatorEnter.ENTER);
    }

    void loadBack()
    {
        NavigatorEnter.loadVista(NavigatorEnter.ENTER);
    }



    @FXML
    void initialize() {


    }
}

