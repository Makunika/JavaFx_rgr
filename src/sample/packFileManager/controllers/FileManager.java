package sample.packFileManager.controllers;


import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.*;
import com.jfoenix.skins.JFXTreeTableViewSkin;
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
import javafx.scene.image.Image;
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

    private TreeTableController treeTableController;

    private ContextMenusController contextMenusController;

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
            stage.setTitle("Bolt Drive");
            stage.getIcons().add(new Image("sample\\resources\\icon\\baseline_cloud_black_18dp.png"));
            stage.setScene(new Scene(root));
            ((Node) (event.getSource())).getScene().getWindow().hide();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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


        iconColumn = new TableColumn<>("");
        nameColumn = new TableColumn<>("Имя");
        sizeColumn = new TableColumn<>("Размер");
        dateColumn = new TableColumn<>("Последнее изменение");
        iconColumn.setStyle("-fx-alignment: CENTER");
        dateColumn.setStyle("-fx-alignment: CENTER-LEFT");
        sizeColumn.setStyle("-fx-alignment: CENTER-LEFT");
        dateColumn.setStyle("-fx-alignment: CENTER-LEFT");
        sizeColumn.setPrefWidth(80);
        sizeColumn.setMinWidth(80);
        sizeColumn.setMaxWidth(80);
        dateColumn.setPrefWidth(180);
        dateColumn.setMinWidth(180);
        dateColumn.setMaxWidth(180);
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
        sizeColumn.setSortable(false);
        dateColumn.comparatorProperty().set(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int result = 0;
                try {
                    Date date1 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(o1);
                    Date date2 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(o2);
                    result = date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return result;
            }
        });

        treeTableController = new TreeTableController(tableView,treeView,backPath,pathName);
        tableView.getColumns().addAll(iconColumn,nameColumn,dateColumn,sizeColumn);
        tableView.getSortOrder().addAll(nameColumn,sizeColumn);

        //загрузка всех контекстных менюшек
        contextMenusController = new ContextMenusController(treeTableController,
                progressUpload,labelDownload,storageLabel,storageProgressBar,labelErr, Holder);

        loginLabel.setText(DataClient.login);

        backPath.setOnAction(event -> {
            //treeView.getSelectionModel().selectPrevious();
            treeView.getSelectionModel().select(treeView.getSelectionModel().getSelectedItem().getParent());
            treeTableController.treeChildToTable();
        });
    }




}

