package sample.packFileManager.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import sample.client.DataClient;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class Settings implements Initializable {

    @FXML
    private AnchorPane pane;

    @FXML
    private JFXButton buttonPicter;

    private boolean yes;
    private boolean no;

    @FXML
    private JFXCheckBox checkBoxPicter;

    private FileManager fileManager;

    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        yes = false;
        no = false;
        if (DataClient.isCustomPicter)
        {
            checkBoxPicter.setSelected(true);
            buttonPicter.setDisable(false);
        }

        checkBoxPicter.setOnAction(event -> {
            if (checkBoxPicter.isSelected())
            {
                buttonPicter.setDisable(false);
                no = false;
            }
            else {
                buttonPicter.setDisable(true);
                no = true;
            }
        });

        buttonPicter.setOnAction(event -> {
            yes = false;
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            File image = fileChooser.showOpenDialog(buttonPicter.getScene().getWindow());
            if (image != null)
            {
                try {
                    File fileSource = new File("custom.png");
                    if (fileSource.exists()) {
                        fileSource.delete();
                    }
                    Files.copy(image.toPath(),fileSource.toPath());
                    yes = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isYes() {
        return yes;
    }

    public boolean isNo() {
        return no;
    }
}
