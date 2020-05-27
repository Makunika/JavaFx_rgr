package sample.packFileManager;

import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sample.client.DataClient;
import sample.connection.*;
import sample.packFileManager.controllers.Rename;
import sample.packFileManager.newtreeitem.FilterableTreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Loadable {

    protected final StackPane               stackPane;
    protected final ProgressIndicator       progressIndicator;
    protected final Label                   labelIndicator;
    protected final TreeTableController     treeTableController;

    protected Loadable(StackPane stackPane, ProgressIndicator progressIndicator, Label labelIndicator, TreeTableController treeTableController) {
        this.stackPane = stackPane;
        this.progressIndicator = progressIndicator;
        this.labelIndicator = labelIndicator;
        this.treeTableController = treeTableController;
    }

    protected void uploadFile(File selectedFile) {
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
                        FilterableTreeItem<DataFile> newFile = new FilterableTreeItem<>(new DataFile("file",
                                selectedFile.getName(),
                                dateFormat.format(new Date()),
                                Long.toString(selectedFile.length())));
                        treeTableController.addItem(newFile);
                        DataClient.storageFill += selectedFile.length();
                        FuncStatic.updateStorage();
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

    protected void uploadPath(File selectedPath) {
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
                        FilterableTreeItem<DataFile> newFile = new FilterableTreeItem<>(new DataFile("path",
                                selectedPath.getName(),
                                dateFormat.format(new Date()),
                                ""));
                        treeTableController.addItem(newFile);
                        recTree(selectedPath,newFile);
                        FuncStatic.updateStorage();
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

    protected void recTree(File fileSource, FilterableTreeItem<DataFile> parent) {
        for (File file : fileSource.listFiles()) {
            try {
                BasicFileAttributes atr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                if (atr.isDirectory()) {
                    String data = atr.creationTime().toString();
                    FilterableTreeItem<DataFile> newPath = new FilterableTreeItem<>(new DataFile(
                            "path",
                            file.getName(),
                            data.replace("T", " ").substring(0,data.length() - 8),
                            ""));
                    parent.getInternalChildren().add(newPath);
                    recTree(file,newPath);
                } else {
                    String data = atr.creationTime().toString();
                    FilterableTreeItem<DataFile> newFile = new FilterableTreeItem<>(new DataFile(
                            "file",
                            file.getName(),
                            data.replace("T", " ").substring(0,data.length() - 8),
                            Long.toString(file.length())));
                    parent.getInternalChildren().add(newFile);
                    DataClient.storageFill += file.length();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Int{
        public long i;

        public Int(long o)
        {
            i = o;
        }
    }

    protected void delete(DataFile item) {
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
                    FuncStatic.updateStorage();
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

    protected void recursiveGetSize(TreeItem<DataFile> item, Int size) {
        for (TreeItem<DataFile> it:
                item.getChildren()) {
            if (!it.getValue().isFile())
            {
                recursiveGetSize(it,size);
            }
            else
            {
                size.i += Long.parseLong(it.getValue().getSize());
            }
        }
    }

    protected void rename(DataFile item) {
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

    protected void downloadFile(DataFile item) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedPath = directoryChooser.showDialog(stackPane.getScene().getWindow());
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

    protected void newPath() {
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

}
