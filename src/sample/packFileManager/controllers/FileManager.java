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
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.cell.TextFieldTableCell;
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
import sample.connection.NetworkData;
import sample.connection.NetworkServiceFileDownload;
import sample.connection.NetworkServiceFileUpload;
import sample.connection.NetworkServiceMessage;
import sample.packEnter.controllers.RememberPasswordController;
import sample.packFileManager.*;

public class FileManager implements Initializable {

    @FXML
    public MenuItem contextMenuLoadPath;

    @FXML
    public MenuItem contextMenuLoad;

    @FXML
    public MenuItem contextMenuRelocaeMaybe;

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
        picterViewer.setImage("sample/resources/picters/123.jpg");
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

        backPath.setVisible(false);

        //настройка таблицы
        //nameColumn.setSortType(TableColumn.SortType.ASCENDING);

        iconColumn = new TableColumn<>("Icon");
        nameColumn = new TableColumn<>("Name");
        sizeColumn = new TableColumn<>("Size");
        dateColumn = new TableColumn<>("Date");


        iconColumn.setCellValueFactory(cellData -> cellData.getValue().iconProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        iconColumn.setPrefWidth(50);
        iconColumn.setMaxWidth(50);
        iconColumn.setMinWidth(50);
        iconColumn.setSortable(false);
        nameColumn.setSortable(false);
        sizeColumn.setSortable(false);
        dateColumn.setSortable(false);




        treeTableController = new TreeTableController(tableView,treeView);
        tableView.getColumns().addAll(iconColumn,nameColumn,dateColumn,sizeColumn);


        //Collections.sort(tableView.getItems(),(Comparator.comparing(DataFile::getName)));

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

                NetworkServiceFileUpload networkServiceFileUpload = new NetworkServiceFileUpload(selectedFile,true,request);

                networkServiceFileUpload.setOnFailed(event1 -> {
                    Platform.runLater(() -> {
                        labelErr.setText("Error");
                        progressUpload.setVisible(false);
                    });
                });

                networkServiceFileUpload.setOnSucceeded(event1 -> {
                    NetworkData networkData = (NetworkData) networkServiceFileUpload.getValue();

                    if (networkData.getCode() == 200) {
                        Platform.runLater(() -> {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
                progressUpload.progressProperty().bind(networkServiceFileUpload.progressProperty());
                networkServiceFileUpload.start();
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


        contextMenuRelocaeMaybe.setDisable(true);


        contextMenuRelocaeMaybe.setOnAction(event -> {
            Moved moved = treeTableController.getMoved();


            String request = "RELOCATE /" +
                    moved.oldPathNameMoved
                    + moved.movedDataFile.getValue().getName() +
                    "//"
                    + pathName.getText().substring(DataClient.login.length()) +
                    moved.movedDataFile.getValue().getName() +
                    "://203";

            NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);

            networkServiceMessage.setOnSucceeded(event1 -> {
                NetworkData networkData = networkServiceMessage.getValue();

                if (networkData.getCode() == 203)
                {
                    Platform.runLater(() -> {
                        treeTableController.initMoved();

                    });
                }
                else {
                    Platform.runLater(() -> {
                        labelErr.setText("fail relocate");
                    });
                }
                Platform.runLater(() -> {
                    contextMenuRelocaeMaybe.setDisable(true);
                });
            });

            networkServiceMessage.setOnFailed(event1 -> {
                Platform.runLater(() -> {
                    labelErr.setText("lost connection");
                    contextMenuRelocaeMaybe.setDisable(true);
                });
            });

            networkServiceMessage.start();
        });


        tableView.setRowFactory(new Callback<TableView<DataFile>, TableRow<DataFile>>() {
            @Override
            public TableRow<DataFile> call(TableView<DataFile> param) {
                final TableRow<DataFile> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();
                ObservableList<MenuItem> menuItems = FXCollections.observableArrayList(
                        new MenuItem("Скачать"),
                        new MenuItem("Удалить"),
                        new MenuItem("Переименовать"),
                        new MenuItem("Переместить")
                );

                //Скачать
                menuItems.get(0).setOnAction(event -> {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    File selectedPath = directoryChooser.showDialog((Stage) pane.getScene().getWindow());
                    if (selectedPath != null)
                    {
                        String nameFile = row.getItem().getName();

                        String request = "DOWNLOAD /" +
                                pathName.getText().substring(DataClient.login.length()) + nameFile +
                                "://201";


                        NetworkServiceFileDownload networkServiceFileDownload = new NetworkServiceFileDownload(
                                selectedPath,
                                true,
                                request,
                                nameFile);



                        networkServiceFileDownload.setOnFailed(event1 -> {
                            Platform.runLater(() -> {
                                labelErr.setText("Download Failed");
                                progressUpload.setVisible(false);
                            });
                        });

                        networkServiceFileDownload.setOnSucceeded(event1 -> {
                            NetworkData networkData = (NetworkData)networkServiceFileDownload.getValue();

                            if (networkData.getCode() == 201)
                            {
                                Platform.runLater(() -> {
                                    try {
                                        Runtime.getRuntime().exec("explorer.exe /select," + selectedPath.getAbsolutePath() + "\\" + nameFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    progressUpload.setVisible(false);
                                });
                            }
                            else
                            {
                                Platform.runLater(() -> {
                                    labelErr.setText("Download Failed");
                                    progressUpload.setVisible(false);
                                });
                            }
                        });

                        progressUpload.progressProperty().bind(networkServiceFileDownload.progressProperty());
                        progressUpload.setVisible(true);
                        networkServiceFileDownload.start();

                    }


                });

                //Удалить
                menuItems.get(1).setOnAction(event -> {

                });


                //Переименовать
                menuItems.get(2).setOnAction(event -> {
                    Platform.runLater(() -> {
                        try {
                            String oldName = row.getItem().getName().toString();
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/resources/scenepack/rename.fxml"));
                            Parent root = (Parent) loader.load();
                            Rename rename = loader.getController();

                            rename.dataFile = row.getItem();
                            rename.setName();
                            Stage stage = new Stage();
                            stage.setTitle("Восстановление пароля");
                            stage.setIconified(false);
                            stage.setScene(new Scene(root));
                            stage.initModality(Modality.WINDOW_MODAL);
                            stage.initOwner(
                                    buttonExitAccount.getScene().getWindow());
                            stage.showAndWait();


                            if (rename.isEdit) {

                                treeTableController.updateTable();
                                treeTableController.updateTree();

                                String request = "RENAME /" +
                                        pathName.getText().substring(DataClient.login.length()) + oldName +
                                        "//" + row.getItem().getName() +
                                        "://202";


                                NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);


                                networkServiceMessage.setOnSucceeded(event1 -> {
                                    NetworkData networkData = networkServiceMessage.getValue();

                                    if (networkData.getCode() != 202)
                                    {
                                        Platform.runLater(() ->{
                                            row.getItem().setName(oldName);
                                            labelErr.setText("Fail Rename");
                                            treeTableController.updateTable();
                                            treeTableController.updateTree();
                                        });
                                    }
                                });


                                networkServiceMessage.setOnFailed(event1 -> {
                                    Platform.runLater(() ->{
                                        row.getItem().setName(oldName);
                                        labelErr.setText("Fail Rename");
                                        treeTableController.updateTable();
                                        treeTableController.updateTree();
                                    });
                                });

                                networkServiceMessage.start();

                            }
                        } catch (IOException ex)
                        {
                            ex.printStackTrace();
                        }
                    });
                });

                //Переместить
                menuItems.get(3).setOnAction(event -> {


                    treeTableController.setMoved(treeTableController.findByDataFile(row.getItem()),
                            pathName.getText().substring(DataClient.login.length()));
                    contextMenuRelocaeMaybe.setDisable(false);
                });


                contextMenu.getItems().addAll(menuItems);

                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                        DataFile item = row.getItem();
                        if (!item.isFile()) {
                            treeView.getSelectionModel().select(treeTableController.findByDataFile(item));
                            treeView.getSelectionModel().getSelectedItem().setExpanded(true);
                            treeTableController.treeChildToTable(pathName, backPath);
                        }
                        else if (item.isPng())
                        {
                            String request = "UPLOAD /" +
                                    pathName.getText().substring(DataClient.login.length()) + item.getName() +
                                    "://201";

                            NetworkServiceFileDownload networkServiceFileDownload = new NetworkServiceFileDownload(
                                    picterViewer.getTmpFile(item.getSuffix()),
                                    true,
                                    request);

                            networkServiceFileDownload.setOnFailed(event1 -> {
                                Platform.runLater(() -> {
                                    labelErr.setText("Fail download");
                                    progressUpload.setVisible(false);
                                });
                            });

                            networkServiceFileDownload.setOnSucceeded(event1 -> {
                                NetworkData networkData = networkServiceFileDownload.getValue();

                                if (networkData.getCode() == 201)
                                {
                                    Platform.runLater(() -> {
                                        progressUpload.setVisible(false);
                                        picterViewer.loadImage();
                                        picterViewer.changeView();
                                    });
                                }
                                else
                                {
                                    Platform.runLater(() -> {
                                        labelErr.setText("Fail download");
                                        progressUpload.setVisible(false);
                                    });
                                }
                            });

                            progressUpload.progressProperty().bind(networkServiceFileDownload.progressProperty());

                            progressUpload.setVisible(true);
                            networkServiceFileDownload.start();

                        }
                    }
                });


                // Set context menu on row, but use a binding to make it only show for non-empty rows:
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu)null)
                                .otherwise(contextMenu)
                );
                return row;
            }
        });




    }

    public void contextMenuTable(ContextMenuEvent contextMenuEvent) {

    }




}

