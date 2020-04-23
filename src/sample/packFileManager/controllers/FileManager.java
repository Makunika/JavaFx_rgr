package sample.packFileManager.controllers;


import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import sample.DataClient;
import sample.connection.GetData;
import sample.connection.NetworkData;
import sample.packFileManager.DataFile;

public class FileManager implements Initializable {

    @FXML
    public MenuItem contextMenuLoadPath;

    @FXML
    public MenuItem contextMenuLoad;

    private ObservableList<DataFile> files;

    @FXML
    private ImageView imageView;

    @FXML
    private Label pathName;

    @FXML
    private Button backPath;

    @FXML
    private ResourceBundle resources;

    @FXML
    private Button b;

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
            root = FXMLLoader.load(getClass().getClassLoader().getResource("sample/packEnter/scenepack/sample.fxml"), resources);
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
        loadImg("sample/packFileManager/123.jpg");
        changeViewImage();
        //Platform.exit();
    }






    @FXML
    void click(MouseEvent event) {
        //loadImg("sample/packFileManager/14124.png");
        //imageView.toFront();
        //changeViewImage();

    }


    void clickedBackImage(MouseEvent mouseEvent) {
        changeViewImage();

    }



    @FXML
    void SelectedTable(MouseEvent event) {

    }

    @FXML
    void SortTable(ActionEvent event) {

    }

    @FXML
    void SelectedNode(MouseEvent event) {
        treeChildToTable();
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pathName.setText(DataClient.login+ "\\");



        double ratio = (double)DataClient.storageFill / (double)DataClient.storageAll;
        storageProgressBar.setProgress(ratio);

        storageLabel.setText(getStringStorage(DataClient.storageFill) + " / " + getStringStorage(DataClient.storageAll));


        //загрузка в tree view
        files = FXCollections.observableArrayList();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TreeItem<DataFile> root = new TreeItem<>(new DataFile("path",DataClient.login,"date","size"));



        Pattern pattern = Pattern.compile("\n");
        parseTree(root,pattern.split(DataClient.tree), new Index(0), 0);

        treeView.setRoot(root);
        treeView.setShowRoot(false);


        //настройка таблицы
        iconColumn.setCellValueFactory(cellData -> cellData.getValue().iconProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        iconColumn.setPrefWidth(50);
        iconColumn.setMaxWidth(50);
        iconColumn.setMinWidth(50);
        iconColumn.setSortable(false);


        //files.add(root.getValue());
        for (TreeItem<DataFile> it : root.getChildren()) {
            files.add(it.getValue());
        }
        tableView.setItems(files);


        //загрузка для прсомотра картинок
        loadImageView();


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
            treeChildToTable();
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

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        try {
                            if (GetData.outDataFile(selectedFile,true,request).getCode() == 200) {

                                Platform.runLater(() -> {
                                    //TODO: Оптимизировать

                                    try {
                                        DataClient.parseTreeFromResponse(GetData.getDataMessage("AUTHORIZATION / ://101").getText());
                                        TreeItem<DataFile> item = treeView.getSelectionModel().getSelectedItem();

                                        TreeItem<DataFile> root = new TreeItem<>(new DataFile("path",DataClient.login,"date","size"));
                                        Pattern pattern = Pattern.compile("\n");
                                        parseTree(root,pattern.split(DataClient.tree), new Index(0), 0);
                                        treeView.setRoot(root);
                                        treeView.getSelectionModel().select(item);
                                        treeChildToTable();
                                    } catch (ConnectException e) {
                                        e.printStackTrace();
                                    }
                                });


                            } else {
                                Platform.runLater(() -> {
                                    labelErr.setText("Слишком большой размер");
                                });
                            }
                        } catch (ConnectException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                new Thread(task).start();
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

                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        try {
                            if (GetData.getDataMessage(request).getCode() == 200) {
                            } else {
                                Platform.runLater(() -> {
                                    labelErr.setText("Слишком большой размер");
                                });
                            }
                        } catch (ConnectException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                new Thread(task).start();
            }
        });

    }

    public void contextMenuTable(ContextMenuEvent contextMenuEvent) {

    }

    private class Index
    {
        public int index;

        public Index(int index)
        {
            this.index = index;
        }
    }


    private void parseTree(TreeItem<DataFile> root, String[] strings, Index index, int rank) {
        Pattern pattern;

        TreeItem<DataFile> newRoot = root;
        while (index.index != strings.length)
        {
            pattern = Pattern.compile("\t");
            String[] stringsItem = pattern.split(strings[index.index]);
            if (Integer.parseInt(stringsItem[0]) > rank)
            {
                parseTree(newRoot, strings, index,rank + 1);
                continue;
            }
            else if (Integer.parseInt(stringsItem[0]) < rank)
            {
                return;
            }
            else
            {
                pattern = Pattern.compile("\\\\");
                String[] regex = pattern.split(stringsItem[2]);
                String type = regex[0].equals("-1") ? "path" : "file";
                String size = regex[0].equals("-1") ? "" : getStringStorage(Long.parseLong(regex[0]));
                TreeItem<DataFile> newItem = null;
                regex[1] = regex[1].replace("T", " ");
                regex[1] = regex[1].substring(0,regex[1].length() - 8);
                newItem = new TreeItem<>(new DataFile(type, stringsItem[1],regex[1], size));
                root.getChildren().add(newItem);
                newRoot = newItem;

            }




            index.index++;
        }

    }

    private void treeChildToTable() {
        TreeItem<DataFile> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null && !item.getValue().isFile()) {
            backPath.setVisible(item.getParent() != null);

            files.clear();

            for (TreeItem<DataFile> it : item.getChildren()) {
                files.add(it.getValue());
            }
            System.out.println(item.toString());

            StringBuffer sb = new StringBuffer("");
            while (item != null) {
                sb.insert(0, item.getValue().getName() + "\\");
                item = item.getParent();
            }
            pathName.setText(sb.toString());
        }
    }

    private void changeViewImage()
    {
        ObservableList<Node> childs = Holder.getChildren();
        if (childs.size() > 1) {
            //
            Node topNode = childs.get(childs.size()-1);
            topNode.toBack();
        }
    }

    private void loadImageView()
    {
        AnchorPane anchorPane = new AnchorPane();

        Pane pane = new Pane();
        pane.setStyle("-fx-background-color:  #696969; -fx-opacity: 0.4");

        pane.setPrefSize(1280,720);
        pane.setLayoutX(0);
        pane.setLayoutY(0);
        anchorPane.getChildren().add(pane);

        Button button = new Button();
        button.setText("Back");
        button.setFont(Font.font(22));
        button.setPrefSize(166,40);
        button.setLayoutX(557);
        button.setLayoutY(630);
        button.setStyle("-fx-background-color: #578eff");
        button.setOnMouseClicked(event -> clickedBackImage(event));
        anchorPane.getChildren().add(button);


        imageView = new ImageView();
        imageView.setStyle("-fx-background-color: #ff0900");
        imageView.setFitWidth(760);
        imageView.setFitHeight(540);
        imageView.setPreserveRatio(true);
        imageView.setLayoutX(260);
        imageView.setLayoutY(45);

        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(760,540);

        stackPane.getChildren().add(imageView);
        stackPane.setAlignment(imageView, Pos.CENTER);

        anchorPane.getChildren().add(stackPane);

        AnchorPane.setTopAnchor(button, (double) 630);
        AnchorPane.setRightAnchor(button, (double) 557);
        AnchorPane.setBottomAnchor(button, (double) 45);

        AnchorPane.setBottomAnchor(pane, (double) 0);
        AnchorPane.setTopAnchor(pane, (double) 0);
        AnchorPane.setRightAnchor(pane, (double) 0);
        AnchorPane.setLeftAnchor(pane, (double) 0);

        AnchorPane.setBottomAnchor(stackPane, (double) 135);
        AnchorPane.setTopAnchor(stackPane, (double) 45);
        AnchorPane.setRightAnchor(stackPane, (double) 260);

        //AnchorPane.setRightAnchor(imageView, (double) 260);
        //AnchorPane.setBottomAnchor(imageView,(double) 135);
        //AnchorPane.setTopAnchor(imageView,(double) 45);


        Holder.getChildren().add(anchorPane);

        changeViewImage();


    }

    private void loadImg(String url)
    {
        imageView.setImage(new Image(url));
    }



    private String getStringStorage(long storage)
    {
        int i = 0;
        while (storage > 1023)
        {
            i++;
            if (i == 4) break;
            storage /= 1024;
        }
        String str = Long.toString(storage);
        switch (i)
        {
            case 0:
            {
                str += " Байт";
                break;
            }
            case 1:
            {
                str += " Кб";
                break;
            }
            case 2:
            {
                str += " Мб";
                break;
            }
            case 3:
            {
                str += " Гб";
                break;
            }
            default: break;
        }
        return str;
    }

}

