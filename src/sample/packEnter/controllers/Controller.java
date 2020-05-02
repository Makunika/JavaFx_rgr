package sample.packEnter.controllers;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.client.DataClient;
import sample.connection.Request;
import sample.connection.Response;
import sample.connection.NetworkServiceMessage;
import sample.packEnter.NavigatorEnter;

public class Controller  {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    public String email;

    public Boolean isCanceled;

    @FXML
    private TextField loginText;

    @FXML
    private Button remembeButton;

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
            label.setText("");

            Request request = new Request("AUTHORIZATION",101);
            NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);

            networkServiceMessage.setOnFailed(event1 -> {
                Platform.runLater(() -> {
                    label.setText("lost connection");
                    progressDownload.setVisible(false);
                });
            });

            networkServiceMessage.setOnSucceeded(e -> {
                Response response = (Response)networkServiceMessage.getValue();
                if (response.isValidCode())
                {
                    DataClient.parseTreeFromResponse(response.getText());
                    Platform.runLater(() -> {
                        //загрузка в тектовик разные данные
                        DataClient.SavedPreferences();
                        //Запуск новой сцены
                        Parent root;
                        try {
                            root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/resources/scenepack/FileManager.fxml"), resources);
                            Stage stage = new Stage();

                            stage.setTitle("File Manager");
                            stage.setScene(new Scene(root, 1280, 720));
                            ((Node) (event.getSource())).getScene().getWindow().hide();
                            stage.show();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                else {
                    Platform.runLater(() -> {
                        label.setText("Неправильный логин или пароль");
                        passwordText.clear();
                        progressDownload.setVisible(false);
                    });
                }
            });


            networkServiceMessage.start();
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

        remembeButton.setOnMouseEntered(event -> {
            remembeButton.setTextFill(Color.web("#8686ff"));
        });
        remembeButton.setOnMouseExited(event -> {
            remembeButton.setTextFill(Color.web("#797979"));
        });

        remembeButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/resources/scenepack/rememberPassword.fxml"));
                Parent root = (Parent) loader.load();
                RememberPasswordController rpc = loader.getController();
                rpc.setEmail(email);
                rpc.setCanceled(isCanceled);
                Stage stage = new Stage();
                stage.setTitle("Восстановление пароля");
                stage.setIconified(false);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(
                        ((Node)event.getSource()).getScene().getWindow() );
                stage.showAndWait();


                isCanceled = rpc.isCanceled;
                email = rpc.email;
                if (!isCanceled)
                {
                    DataClient.login = DataClient.password = "null";
                    Request request = new Request("REMPASS", email,300);
                    NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);

                    networkServiceMessage.setOnFailed(event1 -> {
                        Platform.runLater(() -> {
                            label.setText("lost connection");
                            progressDownload.setVisible(false);
                        });
                    });

                    networkServiceMessage.setOnSucceeded(event1 -> {
                        Response response = networkServiceMessage.getValue();

                        if (response.isValidCode())
                        {
                            Platform.runLater(() -> {
                                label.setText("Проверьте почту " + email);
                                progressDownload.setVisible(false);
                            });
                        }
                        else
                        {
                            Platform.runLater(() -> {
                                label.setText("Fail");
                                progressDownload.setVisible(false);
                            });
                        }

                    });

                    progressDownload.setVisible(true);
                    networkServiceMessage.start();



                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    private void sendController(RememberPasswordController rememberPasswordController)
    {
        rememberPasswordController.mainController = this;
    }


    public void setVista(Node node) {
        Holder.getChildren().setAll(node);
    }


}



