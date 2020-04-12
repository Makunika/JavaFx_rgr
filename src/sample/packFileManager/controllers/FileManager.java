package sample.packFileManager.controllers;


import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.DataClient;
import sample.connection.GetData;
import sample.connection.NetworkData;
import sample.packFileManager.DataFile;

public class FileManager implements Initializable {

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
    private Label loginLabel;

    @FXML
    private StackPane Holder;

    @FXML
    private TableColumn<DataFile, ImageView> iconColumn;

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
        pathName.setText("");
        long storageAll = 0;
        long storageFill = 0;
        try {
            NetworkData networkData = GetData.getDataMessage("STORAGE / ://200");
            if (networkData.getCode() == 200) {
                Pattern pattern = Pattern.compile("/");
                String[] strings = pattern.split(networkData.getText());
                storageAll = Long.parseLong(strings[0]);
                storageFill = Long.parseLong(strings[1]);
            }
        } catch (ConnectException e)
        {
            e.printStackTrace();
        }


        double ratio = (double)storageFill / (double)storageAll;
        storageProgressBar.setProgress(ratio);

        storageLabel.setText(getStorage(storageFill) + " / " + getStorage(storageAll));



        try {
            //загрузка в tree view
            files = FXCollections.observableArrayList();
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            TreeItem<DataFile> root = new TreeItem<>(new DataFile("file:data1:23.01:245 kb"));
            ObservableList<TreeItem<DataFile>> rootlist = FXCollections.observableArrayList(
                    new TreeItem<>(new DataFile("d123d:data2:23.01:245 kb")),
                    new TreeItem<>(new DataFile("file:data3:23.01:245 kb")),
                    new TreeItem<>(new DataFile("d123d:data4:23.01:245 kb"))
            );
            root.getChildren().addAll(rootlist);
            TreeItem<DataFile> data31 = new TreeItem<>(new DataFile("file:data41:23.01:245 kb"));
            rootlist.get(2).getChildren().add(data31);
            treeView.setRoot(root);


            //настройка таблицы
            iconColumn.setCellValueFactory(cellData -> cellData.getValue().iconProperty());
            nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
            dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

            iconColumn.setPrefWidth(50);
            iconColumn.setMaxWidth(50);
            iconColumn.setMinWidth(50);
            iconColumn.setSortable(false);


            files.add(root.getValue());
            for (TreeItem<DataFile> it : root.getChildren()) {
                files.add(it.getValue());
            }
            tableView.setItems(files);


            //загрузка для прсомотра картинок
            loadImageView();


        } catch (IOException e) {
            e.printStackTrace();
        }

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
    }

    private void treeChildToTable() {
        TreeItem<DataFile> item = treeView.getSelectionModel().getSelectedItem();
        backPath.setVisible(item.getParent() != null);

        files.clear();

        for (TreeItem<DataFile> it: item.getChildren()) {
            files.add(it.getValue());
        }
        System.out.println(item.toString());

        StringBuffer sb = new StringBuffer("");
        while (item != null)
        {
            sb.insert(0,item.getValue().getName() + "\\");
            item = item.getParent();
        }
        pathName.setText(sb.toString());
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



    private String getStorage(long storage)
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

