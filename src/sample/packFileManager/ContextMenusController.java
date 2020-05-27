package sample.packFileManager;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sample.client.DataClient;
import sample.client.SVGIcons;
import sample.connection.*;
import sample.packFileManager.controllers.Rename;
import sample.packFileManager.viewers.MediaViewer;
import sample.packFileManager.viewers.PicterViewer;
import sample.packFileManager.viewers.TextViewer;
import sample.packFileManager.viewers.Viewer;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContextMenusController extends Loadable {
    private final Label                 labelErr;
    private final PicterViewer          picterViewer;
    private final TextViewer            textViewer;
    private final MediaViewer           mediaViewer;
    private MyPopup                     popupInRow;
    private MyPopup                     popupNotRow;

    public ContextMenusController(TreeTableController treeTableController,
                                  ProgressIndicator progressIndicator, Label labelIndicator,
                                  Label labelErr, StackPane stackPane)
    {
        super(stackPane, progressIndicator, labelIndicator, treeTableController);
        this.labelErr = labelErr;
        this.picterViewer = new PicterViewer(stackPane);
        this.textViewer = new TextViewer(stackPane);
        this.mediaViewer = new MediaViewer(stackPane);
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
                new MySVGPath(SVGIcons.ADD),
                new MySVGPath(SVGIcons.ADD),
                new MySVGPath(SVGIcons.MOVE),
                new MySVGPath(SVGIcons.CREATE_NEW_FOLDER),
                new MySVGPath(SVGIcons.ADD),
                new MySVGPath(SVGIcons.ADD),
                new MySVGPath(SVGIcons.MOVE)
        );
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
                new JFXButton("Переместить", svgs.get(3)),
                new JFXButton("Создать новую папку", svgs.get(8)),
                new JFXButton("Загрузить файл", svgs.get(9)),
                new JFXButton("Загрузить папку", svgs.get(10)),
                new JFXButton("Переместить сюда...", svgs.get(11))
        );
        ObservableList<JFXButton> notRow = FXCollections.observableArrayList(
                new JFXButton("Создать новую папку", svgs.get(4)),
                new JFXButton("Загрузить файл", svgs.get(6)),
                new JFXButton("Загрузить папку", svgs.get(5)),
                new JFXButton("Переместить сюда...", svgs.get(7))
        );
        popupNotRow = new MyPopup(notRow,200);
        popupInRow = new MyPopup(inRow, 200);
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
        popupNotRow.get(2).setOnAction(event -> {
            popupNotRow.hide();
            DirectoryChooser dc = new DirectoryChooser();
            dc.setTitle("Выберете папку");
            File selectedPath = dc.showDialog(stackPane.getScene().getWindow());
            uploadPath(selectedPath);
        });


        //Загрузить файл
        popupNotRow.get(1).setOnAction(event -> {
            popupNotRow.hide();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберете файл");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Files", "*.*"),
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                    new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
            File selectedFile = fileChooser.showOpenDialog(stackPane.getScene().getWindow());
            uploadFile(selectedFile);
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
                popupInRow.get(7).setDisable(false);
            });


            //Новая папка
            popupInRow.get(5).setOnAction(event -> {
                popupInRow.hide();
                newPath();
            });


            //Загрузить папку
            popupInRow.get(4).setOnAction(event -> {
                popupInRow.hide();
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Выберете папку");
                File selectedPath = dc.showDialog(stackPane.getScene().getWindow());
                uploadPath(selectedPath);
            });


            //Загрузить файл
            popupInRow.get(6).setOnAction(event -> {
                popupInRow.hide();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Выберете файл");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("All Files", "*.*"),
                        new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
                File selectedFile = fileChooser.showOpenDialog(stackPane.getScene().getWindow());
                uploadFile(selectedFile);
            });


            //Переместить сюда...
            popupInRow.get(7).setDisable(true);
            popupInRow.get(7).setOnAction(event -> {
                popupInRow.hide();
                relocate();
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
                popupInRow.get(7).setDisable(true);
            });
        });

        networkServiceMessage.setOnFailed(event1 -> {
            Platform.runLater(() -> {
                new Alert(stackPane).show();
                popupNotRow.get(3).setDisable(true);
                popupInRow.get(7).setDisable(true);
            });
        });

        networkServiceMessage.start();
    }



}
