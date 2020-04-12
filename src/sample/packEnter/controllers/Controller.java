package sample.packEnter.controllers;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
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
    private ProgressIndicator progressDownload;

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

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        if (GetData.getDataMessage("AUTHORIZATION / ://101").getCode() == 100)
                        {
                            Platform.runLater(() -> {
                                //загрузка в тектовик разные данные
                                DataClient.SavedPreferences();
                                //Запуск новой сцены
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
                            });
                        } else
                        {
                            Platform.runLater(() -> {
                                label.setText("Неправильный логин или пароль");
                                progressDownload.setVisible(false);
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            new Thread(task).start();
            progressDownload.setVisible(true);




        }




    }



    @FXML
    void signUpClicked(ActionEvent event) {
        DataClient.SavedPreferences();
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
                        checkPassword.setSelected(DataClient.isSavedPassword = line != null && line.equalsIgnoreCase("true"));

                        line = br.readLine();
                        checkAuto.setSelected(DataClient.isAutoEnter = line != null && line.equalsIgnoreCase("true"));

                        line = br.readLine();
                        if (line != null) {
                            DataClient.login = line;
                            loginText.setText(line);
                        }
                        line = br.readLine();
                        if (line != null) {
                            DataClient.password = line;
                            passwordText.setText(line);
                        }

                    }
                }
            }
            else
            {
                filePreferences.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (DataClient.isAutoEnter)
        {
            signIn.fire();
        }
        checkAuto.setOnAction(event -> {
            if(checkAuto.isSelected())
            {
                DataClient.isAutoEnter = true;
                checkPassword.setSelected(true);
                DataClient.isSavedPassword = true;
            }
            else DataClient.isAutoEnter = false;
        });

        checkPassword.setOnAction(event -> {
            DataClient.isSavedPassword = checkPassword.isSelected();
            if (!DataClient.isSavedPassword)
            {
                checkAuto.setSelected(false);
                DataClient.isAutoEnter = false;
            }
        });

    }

    public void setVista(Node node) {
        Holder.getChildren().setAll(node);
    }


}



