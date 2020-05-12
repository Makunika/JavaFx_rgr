package sample.packFileManager;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXPopup;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.client.DataClient;
import sample.client.SVGIcons;
import sample.client.Timer;
import sample.connection.*;
import sample.packFileManager.controllers.Rename;
import sample.packFileManager.viewers.MediaViewer;
import sample.packFileManager.viewers.PicterViewer;
import sample.packFileManager.viewers.TextViewer;
import sample.packFileManager.viewers.Viewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContextMenusController {
    private final TreeTableController   treeTableController;
    private final ProgressIndicator     progressIndicator;
    private final Label                 labelIndicator;
    private final Label                 labelErr;
    private final Label                 storageLabel;
    private final ProgressBar           storageProgress;
    private final PicterViewer          picterViewer;
    private final TextViewer            textViewer;
    private final MediaViewer           mediaViewer;
    private final StackPane             stackPane;
    private MyPopup                     popupInRow;
    private MyPopup                     popupNotRow;

    public ContextMenusController(TreeTableController treeTableController,
                                  ProgressIndicator progressIndicator, Label labelIndicator , Label storageLabel,
                                  ProgressBar storageProgress, Label labelErr, StackPane stackPane)
    {
        this.progressIndicator = progressIndicator;
        this.treeTableController = treeTableController;
        this.labelErr = labelErr;
        this.storageProgress = storageProgress;
        this.storageLabel = storageLabel;
        this.picterViewer = new PicterViewer(stackPane);
        this.textViewer = new TextViewer(stackPane);
        this.mediaViewer = new MediaViewer(stackPane);
        this.stackPane = stackPane;
        this.labelIndicator = labelIndicator;
        load();
    }

    private void load() {
        //Загрузка попапов
        MySVGPath download = new MySVGPath(SVGIcons.GET_APP);
        ObservableList<MySVGPath> svgs = FXCollections.observableArrayList(
                new MySVGPath(SVGIcons.GET_APP),
                new MySVGPath(SVGIcons.DELETE),
                new MySVGPath(SVGIcons.EDIT),
                new MySVGPath(SVGIcons.MOVE),
                new MySVGPath(SVGIcons.CREATE_NEW_FOLDER),
                new MySVGPath(SVGIcons.GET_APP),
                new MySVGPath(SVGIcons.GET_APP),
                new MySVGPath(SVGIcons.MOVE)
        );
        svgs.get(5).setRotate(180);
        svgs.get(6).setRotate(180);
        String str = "#99999e";
        for (MySVGPath svg: svgs) {
            svg.setStyle("-fx-fill: #79a6f2; -fx-alignment: center-left");
            svg.setScaleX(0.8);
            svg.setScaleY(0.8);
        }

        ObservableList<JFXButton> inRow = FXCollections.observableArrayList(
                new JFXButton("Скачать", svgs.get(0)),
                new JFXButton("Удалить", svgs.get(1)),
                new JFXButton("Переименовать", svgs.get(2)),
                new JFXButton("Переместить", svgs.get(3))
        );
        popupInRow = new MyPopup(inRow, 200);
        ObservableList<JFXButton> notRow = FXCollections.observableArrayList(
                new JFXButton("Создать новую папку", svgs.get(4)),
                new JFXButton("Загрузить папку", svgs.get(5)),
                new JFXButton("Загрузить файл", svgs.get(6)),
                new JFXButton("Переместить сюда...", svgs.get(7))
        );
        popupNotRow = new MyPopup(notRow,200);
        //загрузка иx действий
        loadNotRow();
        loadInRow();
    }

    private void loadNotRow() {

        treeTableController.getRefTableView().setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.SECONDARY) && treeTableController.getRefTableView().getItems().isEmpty())
            {
                popupNotRow.show(treeTableController.getRefTableView(),event.getX(),event.getY());
            }
        });

        //Новая папка
        popupNotRow.get(0).setOnAction(event -> {
            popupNotRow.hide();
            newPath();
        });


        //Загрузить папку
        popupNotRow.get(1).setOnAction(event -> {
            popupNotRow.hide();
            uploadPath();
        });


        //Загрузить
        popupNotRow.get(2).setOnAction(event -> {
            popupNotRow.hide();
            uploadFile();
        });


        //Переместить сюда...
        popupNotRow.get(3).setDisable(true);
        popupNotRow.get(3).setOnAction(event -> {
            popupNotRow.hide();
            relocate();
        });
    }

    private void loadInRow() {
        treeTableController.getRefTableView().setRowFactory(call -> {
            final TableRow<DataFile> row = new TableRow<>();
            row.setPrefHeight(35);
            //Скачать
            popupInRow.get(0).setOnAction(event -> {
                popupInRow.hide();
                downloadFile(treeTableController.getRefTableView().getSelectionModel().getSelectedItem());
            });


            //Удалить
            popupInRow.get(1).setOnAction(event -> {
                popupInRow.hide();
                delete(treeTableController.getRefTableView().getSelectionModel().getSelectedItem());
            });


            //Переименовать
            popupInRow.get(2).setOnAction(event -> {
                popupInRow.hide();
                rename(treeTableController.getRefTableView().getSelectionModel().getSelectedItem());
            });


            //Переместить
            popupInRow.get(3).setOnAction(event -> {
                treeTableController.setMoved(treeTableController.findByDataFile(treeTableController.getRefTableView().getSelectionModel().getSelectedItem()),
                        treeTableController.getPathName().getText().substring(DataClient.login.length()));
                popupInRow.hide();
                //переместить сюда... становится видимым
                popupNotRow.get(3).setDisable(false);
            });



            //двойной клик
            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                    DataFile item = row.getItem();
                    if (item != null) {
                        if (!item.isFile()) {
                            treeTableController.getRefTreeView().getSelectionModel().select(treeTableController.findByDataFile(item));
                            treeTableController.getRefTreeView().getSelectionModel().getSelectedItem().setExpanded(true);
                            treeTableController.treeChildToTable();
                        } else if (item.isPng()) {
                            picterViewerLoad(item);
                        } else if (item.isTxt()) {
                            txtViewerLoad(item);
                        } else if (item.isMedia()) {
                            mediaViewerLoad(item);
                        }
                    }
                }
                if (event.getButton().equals(MouseButton.SECONDARY))
                {
                    TableRow<DataFile> row_e = (TableRow<DataFile>) event.getSource();
                    if (row_e.isEmpty()) {
                        popupNotRow.show(stackPane, event.getSceneX(), event.getSceneY());
                    } else {
                        popupInRow.show(stackPane, event.getSceneX(), event.getSceneY());
                    }
                }
            });
            return row;
        });

    }

    private class Int{
        public long i;

        public Int(long o)
        {
            i = o;
        }
    }

    private void delete(DataFile item) {
        TreeItem<DataFile> itemInTree = treeTableController.findByDataFile(item);
        Request request = new Request(
                "DELETE",
                treeTableController.getPathName().getText().substring(DataClient.login.length()) + itemInTree.getValue().getName(),
                205);

        NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);

        networkServiceMessage.setOnSucceeded(event -> {
            Response response = networkServiceMessage.getValue();

            if (response.isValidCode())
            {
                Platform.runLater(() -> {
                    treeTableController.deleteItem(item);
                    if (itemInTree.getValue().isFile()) {
                        DataClient.storageFill -= Long.parseLong(itemInTree.getValue().getSize());
                    } else {
                        Int size = new Int(0);
                        recursiveGetSize(itemInTree, size);
                        DataClient.storageFill -= size.i;
                    }
                    double ratiox = (double)DataClient.storageFill / (double)DataClient.storageAll;
                    storageProgress.setProgress(ratiox);
                    storageLabel.setText(FuncStatic.getStringStorage(DataClient.storageFill) + " / " + FuncStatic.getStringStorage(DataClient.storageAll));
                });
            }
            else
            {
                Platform.runLater(() -> {
                    new Alert(stackPane,"Ошибка при удалении: " + response.getCode() + " " + response.getText()).show();
                });
            }
        });

        networkServiceMessage.setOnFailed(event -> {
            Platform.runLater(() -> {
                new Alert(stackPane).show();
            });
        });

        networkServiceMessage.start();
    }

    private void recursiveGetSize(TreeItem<DataFile> item, Int size) {
        for (TreeItem<DataFile> it:
             item.getChildren()) {
            if (it.getValue().isFile())
            {
                recursiveGetSize(it,size);
            }
            else
            {
                size.i += Long.parseLong(it.getValue().getSize());
            }
        }
    }

    private void txtViewerLoad(DataFile item)
    {
        viewerLoad(item, textViewer);
    }

    private void picterViewerLoad(DataFile item) {
        viewerLoad(item, picterViewer);
    }

    private void mediaViewerLoad(DataFile item)
    {
        viewerLoad(item, mediaViewer);
    }

    private void viewerLoad(DataFile item, Viewer viewer) {
        Request request = new Request(
                "UPLOAD",
                treeTableController.getPathName().getText().substring(DataClient.login.length()) + item.getName(),
                201);

        NetworkServiceFileDownload networkServiceFileDownload = new NetworkServiceFileDownload(
                viewer.getTmpFile(item.getSuffix()),
                true,
                request);

        networkServiceFileDownload.setOnFailed(event1 -> {
            Platform.runLater(() -> {
                new Alert(stackPane).show();
                progressIndicator.setVisible(false);
                labelIndicator.setVisible(false);
            });
        });

        networkServiceFileDownload.setOnSucceeded(event1 -> {
            Response response = networkServiceFileDownload.getValue();

            if (response.isValidCode()) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    labelIndicator.setVisible(false);
                    viewer.loadBody();
                    viewer.addHeader(item.getName());
                    viewer.dialogView();
                });
            } else {
                Platform.runLater(() -> {
                    new Alert(stackPane, "Ошибка при загрузке: " + response.getCode() + " " + response.getText()).show();
                    progressIndicator.setVisible(false);
                    labelIndicator.setVisible(false);
                });
            }
        });

        progressIndicator.progressProperty().bind(networkServiceFileDownload.progressProperty());
        labelIndicator.textProperty().bind(networkServiceFileDownload.messageProperty());
        progressIndicator.setVisible(true);
        labelIndicator.setVisible(true);
        networkServiceFileDownload.start();
    }

    private void rename(DataFile item) {
        Platform.runLater(() -> {
            try {
                String oldName = item.getName();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/resources/scenepack/rename.fxml"));
                Parent root = loader.load();
                Rename rename = loader.getController();

                rename.dataFile = item;
                rename.setName();
                rename.setPromptText("Новое название");
                JFXDialog dialog = new JFXDialog();
                rename.setParentDialog(dialog);
                dialog.setContent((Region) root);
                dialog.show(stackPane);

                dialog.setOnDialogClosed(event -> {
                    if (rename.isEdit) {

                        treeTableController.updateTable();
                        treeTableController.updateTree();

                        Request request = new Request(
                                "RENAME",
                                treeTableController.getPathName().getText().substring(DataClient.login.length()) + oldName +
                                        "//" + item.getName(),
                                202);

                        NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);


                        networkServiceMessage.setOnSucceeded(event1 -> {
                            Response response = networkServiceMessage.getValue();

                            if (!response.isValidCode()) {
                                Platform.runLater(() -> {
                                    item.setName(oldName);
                                    new Alert(stackPane, "Ошибка при переименовании: " + response.getCode() + " " + response.getText()).show();
                                    treeTableController.updateTable();
                                    treeTableController.updateTree();
                                });
                            }
                        });


                        networkServiceMessage.setOnFailed(event1 -> {
                            Platform.runLater(() -> {
                                item.setName(oldName);
                                new Alert(stackPane).show();
                                treeTableController.updateTable();
                                treeTableController.updateTree();
                            });
                        });

                        networkServiceMessage.start();

                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void downloadFile(DataFile item) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedPath = directoryChooser.showDialog(labelErr.getScene().getWindow());
        if (selectedPath != null) {
            String nameFile = item.getName();
            String nameFileForExplorer = item.isFile() ? nameFile : nameFile + ".zip";

            Request request = new Request(
                    "DOWNLOAD",
                    treeTableController.getPathName().getText().substring(DataClient.login.length()) + nameFile,
                    201);

            NetworkServiceFileDownload networkServiceFileDownload = new NetworkServiceFileDownload(
                    selectedPath,
                    item.isFile(),
                    request,
                    nameFileForExplorer);


            networkServiceFileDownload.setOnFailed(event1 -> {
                Platform.runLater(() -> {
                    new Alert(stackPane).show();
                    labelIndicator.setVisible(false);
                });
            });

            networkServiceFileDownload.setOnSucceeded(event1 -> {
                Response response = networkServiceFileDownload.getValue();

                if (response.isValidCode()) {
                    Platform.runLater(() -> {
                        try {
                            Runtime.getRuntime().exec("explorer.exe /select," + selectedPath.getAbsolutePath() + "\\" + nameFileForExplorer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        progressIndicator.setVisible(false);
                        labelIndicator.setVisible(false);
                    });
                } else {
                    Platform.runLater(() -> {
                        new Alert(stackPane,"Ошибка при загрузке: " + response.getCode() + " " + response.getText()).show();
                        progressIndicator.setVisible(false);
                        labelIndicator.setVisible(false);
                    });
                }
            });

            progressIndicator.progressProperty().bind(networkServiceFileDownload.progressProperty());
            labelIndicator.textProperty().bind(networkServiceFileDownload.messageProperty());
            labelIndicator.setVisible(true);
            progressIndicator.setVisible(true);
            networkServiceFileDownload.start();
        }
    }

    private void newPath() {
        Platform.runLater(() -> {
            try {
                String oldName = "";
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DataFile newPath = new DataFile("path","",dateFormat.format(new Date()),"");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/resources/scenepack/rename.fxml"));
                Parent root = loader.load();
                Rename rename = loader.getController();

                rename.dataFile = newPath;
                rename.setName();
                rename.setPromptText("Название новой папки");
                JFXDialog dialog = new JFXDialog();
                rename.setParentDialog(dialog);
                dialog.setContent((Region) root);
                dialog.show(stackPane);

                dialog.setOnDialogClosed(event -> {
                    if (rename.isEdit) {

                        Request request = new Request(
                                "NEWPATH",
                                treeTableController.getPathName().getText().substring(DataClient.login.length()) +
                                        "//" + newPath.getName(),
                                204);

                        NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);


                        networkServiceMessage.setOnSucceeded(event1 -> {
                            Response response = networkServiceMessage.getValue();

                            if (response.isValidCode()) {
                                Platform.runLater(() -> {
                                    treeTableController.addItem(newPath);
                                });
                            }
                            else {
                                Platform.runLater(() -> {
                                    new Alert(stackPane, "Ошибка при создании новой папки: " + response.getCode() + " " + response.getText()).show();
                                });
                            }
                        });


                        networkServiceMessage.setOnFailed(event1 -> {
                            Platform.runLater(() -> {
                                new Alert(stackPane).show();
                            });
                        });

                        networkServiceMessage.start();

                    }
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void relocate() {
        Moved moved = treeTableController.getMoved();


        Request request = new Request(
                "RELOCATE",
                moved.oldPathNameMoved +
                        moved.movedDataFile.getValue().getName() +
                        "//"
                        + treeTableController.getPathName().getText().substring(DataClient.login.length()) +
                        moved.movedDataFile.getValue().getName(),
                203);


        NetworkServiceMessage networkServiceMessage = new NetworkServiceMessage(request);

        networkServiceMessage.setOnSucceeded(event1 -> {
            Response response = networkServiceMessage.getValue();

            if (response.isValidCode())
            {
                Platform.runLater(treeTableController::initMoved);
            }
            else {
                Platform.runLater(() -> {
                    new Alert(stackPane, "Ошибка при перемещении: " +response.getCode() + " " + response.getText()).show();
                });
            }
            Platform.runLater(() -> {
                popupNotRow.get(3).setDisable(true);
            });
        });

        networkServiceMessage.setOnFailed(event1 -> {
            Platform.runLater(() -> {
                new Alert(stackPane).show();
                popupNotRow.get(3).setDisable(true);
            });
        });

        networkServiceMessage.start();
    }

    private void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберете файл");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
        File selectedFile = fileChooser.showOpenDialog(labelErr.getScene().getWindow());
        if (selectedFile != null) {

            Request request = new Request(
                    "UPLOAD",
                    treeTableController.getPathName().getText().substring(DataClient.login.length()) + selectedFile.getName() +
                            "//" + selectedFile.length() + "//1",
                    200);

            NetworkServiceFileUpload networkServiceFileUpload = new NetworkServiceFileUpload(selectedFile,true,request);

            networkServiceFileUpload.setOnFailed(event1 -> {
                Platform.runLater(() -> {
                    new Alert(stackPane).show();
                    progressIndicator.setVisible(false);
                    labelIndicator.setVisible(false);
                });
            });

            networkServiceFileUpload.setOnSucceeded(event1 -> {
                Response response = networkServiceFileUpload.getValue();

                if (response.isValidCode()) {
                    Platform.runLater(() -> {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        TreeItem<DataFile> newFile = new TreeItem<>(new DataFile("file",
                                selectedFile.getName(),
                                dateFormat.format(new Date()),
                                Long.toString(selectedFile.length())));
                        treeTableController.addItem(newFile);
                        DataClient.storageFill += selectedFile.length();
                        double ratiox = (double)DataClient.storageFill / (double)DataClient.storageAll;
                        storageProgress.setProgress(ratiox);
                        storageLabel.setText(FuncStatic.getStringStorage(DataClient.storageFill) + " / " + FuncStatic.getStringStorage(DataClient.storageAll));
                        progressIndicator.setVisible(false);
                        labelIndicator.setVisible(false);
                    });


                } else {
                    Platform.runLater(() -> {
                        new Alert(stackPane, "Ошибка: " + response.getCode() + " " + response.getText()).show();
                        progressIndicator.setVisible(false);
                        labelIndicator.setVisible(false);
                    });

                }
            });
            progressIndicator.setVisible(true);
            progressIndicator.progressProperty().bind(networkServiceFileUpload.progressProperty());
            labelIndicator.textProperty().bind(networkServiceFileUpload.messageProperty());
            networkServiceFileUpload.start();
            labelIndicator.setVisible(true);
        }
    }

    private void uploadPath() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Выберете папку");
        File selectedPath = dc.showDialog(labelErr.getScene().getWindow());
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
            Request request = new Request(
                    "UPLOAD",
                    treeTableController.getPathName().getText().substring(DataClient.login.length()) + selectedPath.getName() +
                            "//" + storageFill + "//0",
                    200);

            NetworkServiceFileUpload networkServiceFileUpload = new NetworkServiceFileUpload(selectedPath,false,request);

            networkServiceFileUpload.setOnFailed(event1 -> {
                Platform.runLater(() -> {
                    new Alert(stackPane).show();
                    progressIndicator.setVisible(false);
                    labelIndicator.setVisible(false);
                });
            });

            networkServiceFileUpload.setOnSucceeded(event1 -> {
                Response response = networkServiceFileUpload.getValue();

                if (response.isValidCode()) {
                    Platform.runLater(() -> {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        TreeItem<DataFile> newFile = new TreeItem<>(new DataFile("path",
                                selectedPath.getName(),
                                dateFormat.format(new Date()),
                                ""));
                        treeTableController.addItem(newFile);
                        recTree(selectedPath,newFile);
                        double ratiox = (double)DataClient.storageFill / (double)DataClient.storageAll;
                        storageProgress.setProgress(ratiox);
                        storageLabel.setText(FuncStatic.getStringStorage(DataClient.storageFill) + " / " + FuncStatic.getStringStorage(DataClient.storageAll));
                        progressIndicator.setVisible(false);
                        labelIndicator.setVisible(false);
                    });


                } else {
                    Platform.runLater(() -> {
                        new Alert(stackPane, "Ошибка: " + response.getCode() + " " + response.getText()).show();
                        progressIndicator.setVisible(false);
                        labelIndicator.setVisible(false);
                    });

                }
            });
            progressIndicator.setVisible(true);
            progressIndicator.progressProperty().bind(networkServiceFileUpload.progressProperty());
            labelIndicator.textProperty().bind(networkServiceFileUpload.messageProperty());
            networkServiceFileUpload.start();
            labelIndicator.setVisible(true);


        }
    }

    private void recTree(File fileSource, TreeItem<DataFile> parent) {
        for (File file : fileSource.listFiles()) {
            try {
                BasicFileAttributes atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                if (atr.isDirectory()) {
                    String data = atr.creationTime().toString();
                    TreeItem<DataFile> newPath = new TreeItem<>(new DataFile(
                            "path",
                            file.getName(),
                            data.replace("T", " ").substring(0,data.length() - 8),
                            ""));
                    parent.getChildren().add(newPath);
                    recTree(file,newPath);
                } else {
                    String data = atr.creationTime().toString();
                    TreeItem<DataFile> newFile = new TreeItem<>(new DataFile(
                            "file",
                            file.getName(),
                            data.replace("T", " ").substring(0,data.length() - 8),
                            Long.toString(file.length())));
                    parent.getChildren().add(newFile);
                    DataClient.storageFill += file.length();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
