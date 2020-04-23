package sample.packEnter.controllers;

import java.net.ConnectException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import sample.DataClient;
import sample.connection.GetData;
import sample.connection.NetworkData;
import sample.connection.NetworkServiceMessage;
import sample.packEnter.NavigatorEnter;

public class Registration {

    @FXML
    private ResourceBundle resources;

    @FXML
    private ProgressIndicator progressDownload;

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

        if (checkFields())
        {
            DataClient.login = loginText.getText();
            DataClient.password = passwordText.getText();
            DataClient.email = emailText.getText();
            label.setText("");

            NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage("REGISTRATION /" + DataClient.email + "://100");

            networkServiceMessage.setOnSucceeded(event1 -> {
                NetworkData networkData = (NetworkData)networkServiceMessage.getValue();
                if (networkData.getCode() == 100)
                {
                    Platform.runLater(() -> {
                        DataClient.SavedPreferences();
                        NavigatorEnter.loadVista(NavigatorEnter.ENTER);
                    });

                }
                else
                {
                    Platform.runLater(() -> {
                        label.setText("Такой пользователь уже существует");
                        progressDownload.setVisible(false);
                    });
                }
            });

            networkServiceMessage.setOnFailed(event1 -> {
                Platform.runLater(() -> {
                    label.setText("lost connection");
                    progressDownload.setVisible(false);
                });
            });

            networkServiceMessage.start();
            progressDownload.setVisible(true);


        }

    }

    @FXML
    void backClicked(ActionEvent event) {
        NavigatorEnter.loadVista(NavigatorEnter.ENTER);
    }




    @FXML
    void initialize() {


    }



    private boolean checkFields()
    {
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+" +
                "(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@" +
                "(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+" +
                "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\" +
                "[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)" +
                "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|" +
                "[a-z0-9-]*[a-z0-9]:(?:" +
                "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern pattern = Pattern.compile(regex);
        boolean bool = true;
        if (loginText.getText().equals(""))
        {
            loginText.setPromptText("Заполните поле");
            loginText.clear();
            bool = false;
        }
        else if (loginText.getText().length() < 3)
        {
            loginText.setPromptText("Больше трех символов");
            loginText.clear();
            bool = false;
        }
        else if (passwordText.getText().equals(""))
        {
            passwordText.setPromptText("Заполните поле");
            passwordText.clear();
            bool = false;
        }
        else if (passwordText.getText().length() < 3)
        {
            passwordText.setPromptText("Больше трех символов");
            passwordText.clear();
            bool = false;
        }
        else if (emailText.getText().equals(""))
        {
            emailText.setPromptText("Заполните поле");
            emailText.clear();
            bool = false;
        }
        else if (!pattern.matcher(emailText.getText()).matches())
        {
            emailText.setPromptText("Неправильные данные");
            emailText.clear();
            bool = false;
        }
        return bool;
    }
}

