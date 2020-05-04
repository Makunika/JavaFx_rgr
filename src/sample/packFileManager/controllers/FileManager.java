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

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.client.DataClient;
import sample.connection.*;
import sample.packFileManager.*;

public class FileManager implements Initializable {

    @FXML
    public MenuItem contextMenuLoadPath;

    @FXML
    public MenuItem contextMenuLoad;

    @FXML
    public MenuItem contextMenuRelocaeMaybe;

    @FXML
    public ContextMenu contextMenuNotRow;

    private TreeTableController treeTableController;

    private PicterViewer picterViewer;

    private ContextMenusController contextMenusController;

    @FXML
    private MenuItem contextMenuNewPath;

    @FXML
    private Label labelDownload;

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
    private TreeView<DataFile> treeView;

    @FXML
    void exitAccountClicked(ActionEvent event) {
        DataClient.isAutoEnter = false;
        DataClient.SavedPreferences();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/resources/scenepack/sample.fxml"), resources);
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
        Platform.exit();
    }



    @FXML
    void SelectedTable(MouseEvent event) {

    }

    @FXML
    void SortTable(ActionEvent event) {

    }

    @FXML
    void SelectedNode(MouseEvent event) {
        treeTableController.treeChildToTable();
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

        backPath.setVisible(false);

        //настройка таблицы
        //nameColumn.setSortType(TableColumn.SortType.ASCENDING);

        iconColumn = new TableColumn<>("Icon");
        nameColumn = new TableColumn<>("Name");
        sizeColumn = new TableColumn<>("Size");
        dateColumn = new TableColumn<>("Date");


        iconColumn.setCellValueFactory(cellData -> cellData.getValue().iconProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        sizeColumn.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<DataFile, String> param) {
                String size = param.getValue().getSize();
                if (size.equals("")) {
                    return new SimpleStringProperty(size);
                } else {
                    return new SimpleStringProperty(FuncStatic.getStringStorage(Long.parseLong(param.getValue().getSize())));
                }
            }
        });
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        iconColumn.setPrefWidth(50);
        iconColumn.setMaxWidth(50);
        iconColumn.setMinWidth(50);
        iconColumn.setSortable(false);
        nameColumn.setSortable(false);
        sizeColumn.setSortable(false);
        dateColumn.setSortable(false);




        treeTableController = new TreeTableController(tableView,treeView,backPath,pathName);
        tableView.getColumns().addAll(iconColumn,nameColumn,dateColumn,sizeColumn);

        //Collections.sort(tableView.getItems(),(Comparator.comparing(DataFile::getName)));

        //загрузка для прсомотра картинок
        picterViewer = new PicterViewer(Holder);

        //загрузка всех контекстных менюшек
        contextMenusController = new ContextMenusController(contextMenuNotRow,treeTableController,
                progressUpload,labelDownload,storageLabel,storageProgressBar,labelErr,picterViewer);


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
            treeTableController.treeChildToTable();
        });
    }




}

