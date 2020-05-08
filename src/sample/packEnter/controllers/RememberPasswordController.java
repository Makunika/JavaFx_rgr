package sample.packEnter.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
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
    private JFXTextField emailField;


    private JFXDialog parentDialog;


    @FXML
    private Button okButton;

    @FXML
    private Button canselButton;

    @FXML
    private Label labelErr;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        email = "";
        isCanceled = true;
        final Boolean[] valid = {false};
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
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Заполните поле");
        RegexValidator regexValidator = new RegexValidator();
        regexValidator.setMessage("Неправильный email");
        regexValidator.setRegexPattern(regex);
        emailField.getValidators().addAll(regexValidator, validator);


        okButton.setOnAction(event -> {

            if (emailField.validate()) {
                isCanceled = false;
                email = emailField.getText();
                parentDialog.close();
            }
        });

        canselButton.setOnAction(event -> {
            isCanceled = true;
            parentDialog.close();
        });





    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCanceled(Boolean canceled) {
        isCanceled = canceled;
    }

    public void setParentDialog(JFXDialog parentDialog) {
        this.parentDialog = parentDialog;
    }

}
