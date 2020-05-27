package sample.packFileManager.controllers;


import java.io.File;
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
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.client.DataClient;
import sample.client.SVGIcons;
import sample.connection.NetworkServiceMessage;
import sample.connection.Request;
import sample.connection.Response;
import sample.packEnter.controllers.RememberPasswordController;
import sample.packFileManager.*;
import sample.packFileManager.Alert;
import sample.packFileManager.viewers.PicterViewer;
import sample.packFileManager.viewers.TextViewer;

public class FileManager implements Initializable {

    private TreeTableController treeTableController;

    private ContextMenusController contextMenusController;

    private DragAndDrop dragAndDrop;

    @FXML
    private ImageView imageFon;


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
    private JFXButton buttonSetting;

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
    private AnchorPane paneFon;

    @FXML
    private ImageView storageImageView;

    private Image imageFonImage;

    @FXML
    void exitAccountClicked(ActionEvent event) {
        DataClient.isAutoEnter = false;
        DataClient.SavedPreferences();
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/resources/scenepack/sample.fxml"), resources);
            Stage stage = new Stage();
            stage.setTitle("Bolt Drive");
            stage.getIcons().add(new Image("/sample/resources/icon/baseline_cloud_black_18dp.png"));
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
        if (DataClient.isCustomPicter) {
            try {
                imageFonImage = new Image(new File(new File(new File(".").getCanonicalPath()), "custom.png").toURI().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        SVGPath exitAccountIcon = new SVGPath();
        exitAccountIcon.setStyle("-fx-fill: #ffffff");
        exitAccountIcon.setContent(SVGIcons.EXIT_ACCOUNT.getPath());
        buttonExitAccount.setGraphic(exitAccountIcon);
        buttonExitAccount.setText("");

        SVGPath settingIcon = new SVGPath();
        settingIcon.setStyle("-fx-fill: #ffffff");
        settingIcon.setContent(SVGIcons.SETTING.getPath());
        buttonSetting.setGraphic(settingIcon);
        buttonSetting.setText("");

        FuncStatic.initStorage(storageLabel, storageProgressBar);
        FuncStatic.updateStorage();
        progressUpload.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        //загрузка в tree view

        backPath.setVisible(false);

        //настройка таблицы
        //nameColumn.setSortType(TableColumn.SortType.ASCENDING);

        paneFon.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                imageFon.setFitHeight(paneFon.getHeight());
                imageFonResize();
            }
        });

        imageFonResize();
        if (DataClient.isCustomPicter) {
            GaussianBlur blur = new GaussianBlur(15);
            storageImageView.setEffect(blur);
        }

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
                progressUpload, labelDownload, labelErr, Holder);

        //загрузка дроп
        dragAndDrop = new DragAndDrop(Holder, progressUpload, labelDownload, treeTableController);

        loginLabel.setText(DataClient.login);

        backPath.setOnAction(event -> {
            //treeView.getSelectionModel().selectPrevious();
            treeView.getSelectionModel().select(treeView.getSelectionModel().getSelectedItem().getParent());
            treeTableController.treeChildToTable();
        });

        buttonSetting.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/resources/scenepack/settings.fxml"));
                Parent root = (Parent) loader.load();
                Settings settings = (Settings) loader.getController();
                settings.setFileManager(this);
                JFXDialog dialog = new JFXDialog();
                dialog.setContent((Region) root);
                dialog.show(Holder);


                dialog.setOnDialogClosed(event1 -> {
                    if (settings.isYes())
                    {
                        try {
                            DataClient.isCustomPicter = true;
                            imageFonImage = new Image(new File(new File(new File(".").getCanonicalPath()), "custom.png").toURI().toString());
                            imageFonResize();
                            GaussianBlur blur = new GaussianBlur(15);
                            storageImageView.setEffect(blur);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (settings.isNo())
                    {
                        imageFonImage = null;
                        DataClient.isCustomPicter = false;
                        storageImageView.setImage(null);
                        imageFon.setImage(null);
                    }
                    DataClient.SavedPreferences();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void imageFonResize() {
        if (DataClient.isCustomPicter) {
            PixelReader reader = imageFonImage.getPixelReader();
            double ratio = imageFon.getFitHeight() / imageFon.getFitWidth();
            double height = imageFonImage.getHeight();
            double width = height / ratio;
            double x = imageFonImage.getWidth() / 2 - width / 2;
            double y = 0;
            WritableImage newImage = new WritableImage(reader, (int) x, (int) y, (int) width, (int) height);
            imageFon.setPreserveRatio(true);
            imageFon.setImage(newImage);

            PixelReader reader2 = newImage.getPixelReader();
            double ratioY = imageFon.getFitWidth() / newImage.getWidth();
            double ratioX = imageFon.getFitHeight() / newImage.getHeight();
            double heightBlur = storageImageView.getFitHeight() / ratioY;
            double widthBlur = storageImageView.getFitWidth() / ratioX;
            double xBlur = 0;
            double yBlur = 313 / ratioY;
            WritableImage newBlurImage = new WritableImage(reader2, (int) xBlur, (int) yBlur, (int) widthBlur, (int) heightBlur);
            storageImageView.setPreserveRatio(true);
            storageImageView.setImage(newBlurImage);
        }
    }


}

