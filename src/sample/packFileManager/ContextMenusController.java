package sample.packFileManager;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.client.DataClient;
import sample.client.Timer;
import sample.connection.*;
import sample.packFileManager.controllers.FileManager;
import sample.packFileManager.controllers.Rename;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ContextMenusController {
    private final ContextMenu           notRowContextMenu;
    private final TreeTableController   treeTableController;
    private final ProgressIndicator     progressIndicator;
    private final Label                 labelIndicator;
    private final Label                 labelErr;
    private final Label                 storageLabel;
    private final ProgressBar           storageProgress;
    private final PicterViewer          picterViewer;
    private ContextMenu                 contextMenuForTree;

    public ContextMenusController(ContextMenu notRowContextMenu,TreeTableController treeTableController,
                                  ProgressIndicator progressIndicator, Label labelIndicator , Label storageLabel,
                                  ProgressBar storageProgress, Label labelErr, PicterViewer picterViewer)
    {
        this.notRowContextMenu = notRowContextMenu;
        this.progressIndicator = progressIndicator;
        this.treeTableController = treeTableController;
        this.labelErr = labelErr;
        this.storageProgress = storageProgress;
        this.storageLabel = storageLabel;
        this.picterViewer = picterViewer;
        this.labelIndicator = labelIndicator;
        load();
    }

    private void load() {
        loadNotRow();
        loadInRow();
    }


    private void loadInRow() {
        treeTableController.getRefTableView().setRowFactory(call -> {
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
                        downloadFile(row.getItem());
                });

                //Удалить
                menuItems.get(1).setOnAction(event -> {
                    delete(row.getItem());
                });

                //Переименовать
                menuItems.get(2).setOnAction(event -> {
                    rename(row.getItem());
                });

                //Переместить
                menuItems.get(3).setOnAction(event -> {
                    treeTableController.setMoved(treeTableController.findByDataFile(row.getItem()),
                            treeTableController.getPathName().getText().substring(DataClient.login.length()));
                    //переместить сюда... становиться видимым
                    notRowContextMenu.getItems().get(3).setDisable(false);
                });


                contextMenu.getItems().addAll(menuItems);

                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && event.getButton().equals(MouseButton.PRIMARY)) {
                        DataFile item = row.getItem();
                        if (item != null && !item.isFile()) {
                            treeTableController.getRefTreeView().getSelectionModel().select(treeTableController.findByDataFile(item));
                            treeTableController.getRefTreeView().getSelectionModel().getSelectedItem().setExpanded(true);
                            treeTableController.treeChildToTable();
                        } else if (item != null && item.isPng()) {
                            picterViewerLoad(item);
                        }
                    }
                });


                // Set context menu on row, but use a binding to make it only show for non-empty rows:
                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(contextMenu)
                );
                return row;
            }
        );

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
                    labelErr.setText("Delete error");
                    Timer timer = new Timer(labelErr,6);
                });
            }
        });

        networkServiceMessage.setOnFailed(event -> {
            Platform.runLater(() -> {
                labelErr.setText("lost connection");
                Timer timer = new Timer(labelErr,6);
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

    private void picterViewerLoad(DataFile item) {
        Request request = new Request(
                "UPLOAD",
                treeTableController.getPathName().getText().substring(DataClient.login.length()) + item.getName(),
                201);

        NetworkServiceFileDownload networkServiceFileDownload = new NetworkServiceFileDownload(
                picterViewer.getTmpFile(item.getSuffix()),
                true,
                request);

        networkServiceFileDownload.setOnFailed(event1 -> {
            Platform.runLater(() -> {
                labelErr.setText("lost connection");
                Timer timer = new Timer(labelErr,6);
                progressIndicator.setVisible(false);
            });
        });

        networkServiceFileDownload.setOnSucceeded(event1 -> {
            Response response = networkServiceFileDownload.getValue();

            if (response.isValidCode()) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    picterViewer.loadImage();
                    picterViewer.changeView();
                });
            } else {
                Platform.runLater(() -> {
                    labelErr.setText("Fail download");
                    Timer timer = new Timer(labelErr,6);
                    progressIndicator.setVisible(false);
                });
            }
        });

        progressIndicator.progressProperty().bind(networkServiceFileDownload.progressProperty());

        progressIndicator.setVisible(true);
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
                Stage stage = new Stage();
                stage.setTitle("Восстановление пароля");
                stage.setIconified(false);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(labelErr.getScene().getWindow());
                stage.showAndWait();


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
                                labelErr.setText("Fail Rename");
                                Timer timer = new Timer(labelErr,6);
                                treeTableController.updateTable();
                                treeTableController.updateTree();
                            });
                        }
                    });


                    networkServiceMessage.setOnFailed(event1 -> {
                        Platform.runLater(() -> {
                            item.setName(oldName);
                            labelErr.setText("lost connection");
                            Timer timer = new Timer(labelErr,6);
                            treeTableController.updateTable();
                            treeTableController.updateTree();
                        });
                    });

                    networkServiceMessage.start();

                }
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
                    labelErr.setText("lost connection");
                    Timer timer = new Timer(labelErr,6);
                    progressIndicator.setVisible(false);
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
                        labelErr.setText("Download Failed");
                        Timer timer = new Timer(labelErr,6);
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

    private void loadNotRow() {

        //Новая папка
        notRowContextMenu.getItems().get(0).setOnAction(event -> {
            newPath();
        });


        //TODO: Загрузить папку
        notRowContextMenu.getItems().get(1).setOnAction(event -> {
            uploadPath();
        });


        //Загрузить
        notRowContextMenu.getItems().get(2).setOnAction(event -> {
            uploadFile();
        });


        //Переместить сюда...
        notRowContextMenu.getItems().get(3).setDisable(true);
        notRowContextMenu.getItems().get(3).setOnAction(event -> {
            relocate();
        });
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
                Stage stage = new Stage();
                stage.setTitle("Новая папка");
                stage.setIconified(false);
                stage.setScene(new Scene(root));
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initOwner(labelErr.getScene().getWindow());
                stage.showAndWait();


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
                                labelErr.setText("Fail create new path");
                                Timer timer = new Timer(labelErr,6);
                            });
                        }
                    });


                    networkServiceMessage.setOnFailed(event1 -> {
                        Platform.runLater(() -> {
                            labelErr.setText("lost connection");
                            Timer timer = new Timer(labelErr,6);
                        });
                    });

                    networkServiceMessage.start();

                }
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
                    labelErr.setText("Fail relocate");
                    Timer timer = new Timer(labelErr,6);
                });
            }
            Platform.runLater(() -> {
                notRowContextMenu.getItems().get(3).setDisable(true);
            });
        });

        networkServiceMessage.setOnFailed(event1 -> {
            Platform.runLater(() -> {
                labelErr.setText("lost connection");
                Timer timer = new Timer(labelErr,6);
                notRowContextMenu.getItems().get(3).setDisable(true);
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
                    labelErr.setText("lost connection");
                    Timer timer = new Timer(labelErr,6);
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
                        labelErr.setText("Слишком большой размер");
                        Timer timer = new Timer(labelErr,6);
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
                    labelErr.setText("lost connection");
                    Timer timer = new Timer(labelErr,6);
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
                        labelErr.setText("Слишком большой размер");
                        Timer timer = new Timer(labelErr,6);
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
