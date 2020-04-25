package sample.packFileManager.controllers;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.client.DataClient;
import sample.connection.NetworkData;
import sample.connection.NetworkServiceFile;
import sample.packFileManager.DataFile;
import sample.packFileManager.FuncStatic;
import sample.packFileManager.PicterViewer;
import sample.packFileManager.TreeTableController;

public class FileManager implements Initializable {

    @FXML
    public MenuItem contextMenuLoadPath;

    @FXML
    public MenuItem contextMenuLoad;

    private TreeTableController treeTableController;

    private PicterViewer picterViewer;

    @FXML
    private MenuItem contextMenuNewPath;

    @FXML
    private Label pathName;

    @FXML
    private Button backPath;

    @FXML
    private ProgressIndicator progressUpload;

    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane pane;

    @FXML
    private Button buttonExit;

    @FXML
    private Button buttonExitAccount;

    @FXML
    private ProgressBar storageProgressBar;

    @FXML
    private Label storageLabel;

    @FXML
    private Label labelErr;

    @FXML
    private Label loginLabel;

    @FXML
    private StackPane Holder;

    @FXML
    private TableColumn<DataFile, String> iconColumn;

    @FXML
    private TableColumn<DataFile, String> nameColumn;

    @FXML
    private TableColumn<DataFile, String> dateColumn;

    @FXML
    private TableColumn<DataFile, String> sizeColumn;

    @FXML
    private TableView<DataFile> tableView;

    @FXML
    private URL location;

    @FXML
    private TreeView<DataFile> treeView;

    @FXML
    void exitAccountClicked(ActionEvent event) {
        //Close current\

        DataClient.isAutoEnter = false;
        DataClient.SavedPreferences();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("/sample/resources/scenepack/sample.fxml"), resources);
            Stage stage = new Stage();
            stage.setTitle("Авторизация");
            stage.setScene(new Scene(root));
            ((Node) (event.getSource())).getScene().getWindow().hide();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exitClicked(ActionEvent event) {
        picterViewer.loadImg("sample/resources/picters/123.jpg");
        picterViewer.changeView();
        //Platform.exit();
    }






    @FXML
    void click(MouseEvent event) {
        //loadImg("sample/packFileManager/14124.png");
        //imageView.toFront();
        //changeViewImage();

    }



    @FXML
    void SelectedTable(MouseEvent event) {

    }

    @FXML
    void SortTable(ActionEvent event) {

    }

    @FXML
    void SelectedNode(MouseEvent event) {
        treeTableController.treeChildToTable(pathName,backPath);
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pathName.setText(DataClient.login+ "\\");

        progressUpload.setVisible(false);
        labelErr.setText("");

        double ratio = (double)DataClient.storageFill / (double)DataClient.storageAll;
        storageProgressBar.setProgress(ratio);

        storageLabel.setText(FuncStatic.getStringStorage(DataClient.storageFill) + " / " + FuncStatic.getStringStorage(DataClient.storageAll));


        //загрузка в tree view

        treeTableController = new TreeTableController(tableView,treeView);

        //настройка таблицы
        iconColumn.setCellValueFactory(cellData -> cellData.getValue().iconProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        iconColumn.setPrefWidth(50);
        iconColumn.setMaxWidth(50);
        iconColumn.setMinWidth(50);
        iconColumn.setSortable(false);


        //загрузка для прсомотра картинок
        picterViewer = new PicterViewer(Holder);


        buttonExitAccount.setOnMouseEntered(event -> {
            buttonExitAccount.setTextFill(Color.AQUA);
        });
        buttonExit.setOnMouseEntered(event -> {
            buttonExit.setTextFill(Color.AQUA);
        });
        buttonExitAccount.setOnMouseExited(event -> {
            buttonExitAccount.setTextFill(Color.WHITE);
        });
        buttonExit.setOnMouseExited(event -> {
            buttonExit.setTextFill(Color.WHITE);
        });
        loginLabel.setText(DataClient.login);

        backPath.setOnAction(event -> {
            //treeView.getSelectionModel().selectPrevious();
            treeView.getSelectionModel().select(treeView.getSelectionModel().getSelectedItem().getParent());
            treeTableController.treeChildToTable(pathName,backPath);
        });

        contextMenuLoad.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберете файл");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Files", "*.*"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                    new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
            File selectedFile = fileChooser.showOpenDialog((Stage) pane.getScene().getWindow());
            if (selectedFile != null) {

                String request = "LOAD /" +
                        pathName.getText().substring(DataClient.login.length()) + selectedFile.getName() +
                        "//" + selectedFile.length() +
                        "//1" +
                        "://200";

                NetworkServiceFile networkServiceFile = new NetworkServiceFile(selectedFile,true,request);

                networkServiceFile.setOnFailed(event1 -> {
                    Platform.runLater(() -> {
                        labelErr.setText("Error");
                        progressUpload.setVisible(false);
                    });
                });

                networkServiceFile.setOnSucceeded(event1 -> {
                    NetworkData networkData = (NetworkData)networkServiceFile.getValue();

                    if (networkData.getCode() == 200) {
                        Platform.runLater(() -> {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            TreeItem<DataFile> newFile = new TreeItem<>(new DataFile("file",
                                    selectedFile.getName(),
                                    dateFormat.format(new Date()),
                                    FuncStatic.getStringStorage(selectedFile.length())));
                            treeTableController.getParent().getChildren().add(newFile);
                            treeTableController.add(newFile);
                            DataClient.storageFill += selectedFile.length();
                            double ratiox = (double)DataClient.storageFill / (double)DataClient.storageAll;
                            storageProgressBar.setProgress(ratiox);
                            storageLabel.setText(FuncStatic.getStringStorage(DataClient.storageFill) + " / " + FuncStatic.getStringStorage(DataClient.storageAll));
                            progressUpload.setVisible(false);
                        });


                    } else {
                        Platform.runLater(() -> {
                            labelErr.setText("Слишком большой размер");
                            progressUpload.setVisible(false);
                        });

                    }
                });
                progressUpload.setVisible(true);
                progressUpload.progressProperty().bind(networkServiceFile.progressProperty());
                networkServiceFile.start();
            }
        });


        contextMenuLoadPath.setOnAction(event -> {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Выберете папку");
            File selectedPath = dc.showDialog((Stage) pane.getScene().getWindow());
            if (selectedPath != null) {

                long storageFill = 0;
                try {
                    storageFill= Files.walk(Paths.get(selectedPath.toURI()),Integer.MAX_VALUE)
                            .filter(p -> p.toFile().isFile())
                            .mapToLong(p -> p.toFile().length())
                            .sum();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String request = "LOAD /" +
                        pathName.getText().substring(DataClient.login.length()) + selectedPath.getName() +
                        "//" + storageFill +
                        "//0" +
                        "://200";
            }
        });


        contextMenuNewPath.setOnAction(event -> {

        });

    }

    public void contextMenuTable(ContextMenuEvent contextMenuEvent) {

    }




}

