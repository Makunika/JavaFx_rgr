package sample.packFileManager.controllers;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.*;
import com.jfoenix.svg.SVGGlyph;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.client.DataClient;
import sample.client.SVGIcons;
import sample.packFileManager.*;
import sample.packFileManager.viewers.PicterViewer;
import sample.packFileManager.viewers.TextViewer;

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

    private ContextMenusController contextMenusController;

    @FXML
    private MenuItem contextMenuNewPath;

    @FXML
    private Label labelDownload;

    @FXML
    private Label pathName;

    @FXML
    private JFXButton backPath;


    @FXML
    private ResourceBundle resources;

    @FXML
    private AnchorPane pane;

    @FXML
    private JFXButton buttonExitAccount;

    @FXML
    private JFXSpinner progressUpload;

    @FXML
    private JFXProgressBar storageProgressBar;

    @FXML
    private Label storageLabel;

    @FXML
    private Label labelErr;

    @FXML
    private Label loginLabel;

    @FXML
    private StackPane Holder;

    @FXML
    private TableColumn<DataFile, SVGPath> iconColumn;

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
        SVGIconsLoaded.init();
        pathName.setText(DataClient.login+ "\\");
        progressUpload.setVisible(false);
        labelErr.setText("");

        SVGPath exitAccountIcon = new SVGPath();
        exitAccountIcon.setStyle("-fx-fill: #ffffff");
        exitAccountIcon.setContent(SVGIcons.EXIT_ACCOUNT.getPath());
        buttonExitAccount.setGraphic(exitAccountIcon);
        buttonExitAccount.setText("");



        double ratio = (double)DataClient.storageFill / (double)DataClient.storageAll;
        storageProgressBar.setProgress(ratio);

        storageLabel.setText(FuncStatic.getStringStorage(DataClient.storageFill) + " / " + FuncStatic.getStringStorage(DataClient.storageAll));
        progressUpload.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        //загрузка в tree view

        backPath.setVisible(false);

        //настройка таблицы
        //nameColumn.setSortType(TableColumn.SortType.ASCENDING);


        iconColumn = new TableColumn<>("Icon");
        nameColumn = new TableColumn<>("Name");
        sizeColumn = new TableColumn<>("Size");
        dateColumn = new TableColumn<>("Date");
        iconColumn.setStyle("-fx-alignment: CENTER");

        tableView.setContextMenu(null);
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


        //загрузка всех контекстных менюшек
        contextMenusController = new ContextMenusController(contextMenuNotRow,treeTableController,
                progressUpload,labelDownload,storageLabel,storageProgressBar,labelErr, Holder);

        loginLabel.setText(DataClient.login);

        backPath.setOnAction(event -> {
            //treeView.getSelectionModel().selectPrevious();
            treeView.getSelectionModel().select(treeView.getSelectionModel().getSelectedItem().getParent());
            treeTableController.treeChildToTable();
        });
    }




}

