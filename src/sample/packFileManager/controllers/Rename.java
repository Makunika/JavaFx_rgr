package sample.packFileManager.controllers;

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
    private TextField nameField;

    @FXML
    private Button okButton;

    @FXML
    private Button canselButton;

    @FXML
    private Label labelErr;

    public DataFile dataFile;


    public boolean isEdit;

    public void setName()
    {
        nameField.setText(dataFile.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        isEdit = false;
        okButton.setOnAction(event -> {
            Pattern pattern = Pattern.compile("\\.");
            String[] strings = pattern.split(nameField.getText());
            if ((strings.length != 1 && dataFile.isFile())
                    || (strings.length == 1 && !dataFile.isFile())) {
                dataFile.setName(nameField.getText());
                isEdit = true;
                Stage stage = (Stage) canselButton.getScene().getWindow();
                stage.close();
            }
            else {
                labelErr.setText("Неправильные данные");
            }
        });

        canselButton.setOnAction(event -> {
            isEdit = false;
            Stage stage = (Stage) canselButton.getScene().getWindow();
            stage.close();
        });


    }
}
