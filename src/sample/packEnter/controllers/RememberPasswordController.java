package sample.packEnter.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RememberPasswordController implements Initializable {
    @FXML
    private ResourceBundle resources;

    public Boolean isCanceled;

    public String email;

    public Controller mainController;

    @FXML
    private URL location;

    @FXML
    private TextField emailField;

    @FXML
    private Button okButton;

    @FXML
    private Button canselButton;

    @FXML
    private Label labelErr;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        email = "pshblo.max@gmail.com";
        emailField.setText(email);
        isCanceled = true;
        okButton.setOnAction(event -> {
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


            if (emailField.getText().equals(""))
            {
                emailField.setPromptText("Заполните поле");
                emailField.clear();
            }
            else if (!pattern.matcher(emailField.getText()).matches())
            {
                emailField.setPromptText("Неправильные данные");
                emailField.clear();
            }
            else {
                isCanceled = false;
                email = emailField.getText();
                Stage stage = (Stage) okButton.getScene().getWindow();
                stage.close();
            }
        });

        canselButton.setOnAction(event -> {
            isCanceled = true;
            Stage stage = (Stage) canselButton.getScene().getWindow();
            stage.close();
        });





    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }
}
