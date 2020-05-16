package sample.packEnter.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import sample.client.DataClient;
import sample.client.Timer;
import sample.connection.Request;
import sample.connection.Response;
import sample.connection.NetworkServiceMessage;
import sample.packEnter.NavigatorEnter;
import sample.packFileManager.DataFile;

public class Registration  {

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane paneReg;

    @FXML
    private URL location;

    @FXML
    private JFXTextField emailText;

    @FXML
    private JFXTextField loginText;

    @FXML
    private JFXPasswordField passwordText;

    @FXML
    private JFXButton signUp;

    @FXML
    private JFXButton back;

    @FXML
    private Label label;

    @FXML
    private JFXSpinner progressDownload;

    @FXML
    void signUpClicked(ActionEvent event) {



        if (loginText.validate() & emailText.validate() & passwordText.validate())
        {
            DataClient.login = loginText.getText();
            DataClient.password = passwordText.getText();
            DataClient.email = emailText.getText();
            label.setText("");

            Request request = new Request("REGISTRATION", DataClient.email,100);
            NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);

            networkServiceMessage.setOnSucceeded(event1 -> {
                Response response = (Response)networkServiceMessage.getValue();
                if (response.isValidCode())
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
                        Timer timer = new Timer(label,10);
                        progressDownload.setVisible(false);
                    });
                }
            });

            networkServiceMessage.setOnFailed(event1 -> {
                Platform.runLater(() -> {
                    label.setText("lost connection");
                    Timer timer = new Timer(label,3);
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
        String regex2 = "\\w{3,25}";

        RegexValidator emailValidator = new RegexValidator();
        RegexValidator textValidator = new RegexValidator();

        emailValidator.setRegexPattern(regex);
        textValidator.setRegexPattern(regex2);

        emailValidator.setMessage("Неправильные данные");
        textValidator.setMessage("Слишком мало или слишком много символов");
        loginText.getValidators().add(textValidator);
        emailText.getValidators().add(emailValidator);
        passwordText.getValidators().add(textValidator);


    }

}

