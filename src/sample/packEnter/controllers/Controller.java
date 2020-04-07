package sample.packEnter.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private CheckBox checkPassword;

    @FXML
    private CheckBox checkAuto;

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

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
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
                        } else
                        {
                            label.setText("Неправильный логин или пароль");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });


        }




    }

    @FXML
    void signUpClicked(ActionEvent event) {
        NavigatorEnter.loadVista(NavigatorEnter.SIGN_UP);
    }

    @FXML
    void initialize() {
        DataClient.isAutoEnter = false;
        DataClient.isSavedPassword = false;


        try {
            File filePreferences = new File(new File(new File(".").getCanonicalPath()),"Preferences.txt");
            if (filePreferences.exists())
            {
                if (filePreferences.canRead())
                {
                    try(BufferedReader br = new BufferedReader(new FileReader(filePreferences))) {
                        String line = br.readLine();
                        if (line != null && line.equalsIgnoreCase("true")) DataClient.isSavedPassword = true;
                        else DataClient.isSavedPassword = false;
                        line = br.readLine();
                        if (line != null && line.equalsIgnoreCase("true")) DataClient.isAutoEnter = true;
                        else DataClient.                        if (line != null && line.equalsIgnoreCase("true")) DataClient.isAutoEnter = true;
 = false;
                        line = br.readLine();
                        if (line != null) loginText.setText(line);
                        line = br.readLine();
                        if (line != null) passwordText.setText(line);





                        String everything = sb.toString();
                    }
                }
            }
            else
            {
                filePreferences.createNewFile()
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        checkAuto.setOnAction(event -> {
            if(checkAuto.isSelected())
            {
                DataClient.isAutoEnter = true;
            }
            else DataClient.isAutoEnter = false;
        });

        checkPassword.setOnAction(event -> {
            if(checkPassword.isSelected())
            {
                DataClient.isSavedPassword = true;
            }
            else DataClient.isSavedPassword = false;
        });

    }

    public void setVista(Node node) {
        Holder.getChildren().setAll(node);
    }


}



