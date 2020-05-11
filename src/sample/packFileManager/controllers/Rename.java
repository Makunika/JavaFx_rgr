package sample.packFileManager.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.packFileManager.DataFile;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class Rename implements Initializable {

    @FXML
    private JFXTextField nameField;

    @FXML
    private JFXButton okButton;

    @FXML
    private JFXButton canselButton;

    private JFXDialog dialog;

    public DataFile dataFile;

    public boolean isEdit;

    public void setName()
    {
        nameField.setText(dataFile.getName());
    }

    public void setPromptText(String value)
    {
        nameField.setPromptText(value);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        isEdit = false;
        RegexValidator validator = new RegexValidator();
        validator.setMessage("Неправильные данные");
        nameField.getValidators().add(validator);
        okButton.setOnAction(event -> {
            validator.setRegexPattern(!dataFile.isFile() ? "[^/?*:;{}\\\\]+" : "[^/?*:;{}\\\\]+\\.[^/?*:;{}\\\\]+");
            if (nameField.validate()) {
                dataFile.setName(nameField.getText());
                isEdit = true;
                dialog.close();
            }
        });

        canselButton.setOnAction(event -> {
            isEdit = false;
            dialog.close();
        });


    }

    public void setDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }

    public void setParentDialog(JFXDialog dialog) {
        this.dialog = dialog;
    }
}
