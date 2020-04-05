package sample.packFileManager.controllers;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import sample.packFileManager.DataFile;

public class FileManager implements Initializable {

    private ObservableList<DataFile> files;

    private ImageView imageView;

    @FXML
    private Button b;

    @FXML
    private AnchorPane pane;

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
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TreeView<DataFile> treeView;


    @FXML
    void click(MouseEvent event) {
        loadImg("sample/packFileManager/14124.png");
        imageView.toFront();
        changeViewImage();

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
            TreeItem<DataFile> item = treeView.getSelectionModel().getSelectedItem();
        files.clear();
        files.add(item.getValue());
        for (TreeItem<DataFile> it: item.getChildren()) {
            files.add(it.getValue());
        }
        System.out.println(item.toString());
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //загрузка в tree view
            files = FXCollections.observableArrayList();
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            TreeItem<DataFile> root = new TreeItem<>(new DataFile("123d:data1:23.01:245 kb"));
            ObservableList<TreeItem<DataFile>> rootlist = FXCollections.observableArrayList(
                    new TreeItem<>(new DataFile("d123d:data2:23.01:245 kb")),
                    new TreeItem<>(new DataFile("d123d:data3:23.01:245 kb")),
                    new TreeItem<>(new DataFile("d123d:data4:23.01:245 kb"))
            );
            root.getChildren().addAll(rootlist);
            TreeItem<DataFile> data31 = new TreeItem<>(new DataFile("d123d:data41:23.01:245 kb"));
            rootlist.get(2).getChildren().add(data31);
            treeView.setRoot(root);


            //настройка таблицы
            iconColumn.setCellValueFactory(new PropertyValueFactory<DataFile, String>("icon1"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<DataFile, String>("name"));
            sizeColumn.setCellValueFactory(new PropertyValueFactory<DataFile, String>("size"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<DataFile, String>("date"));

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
        imageView.setLayoutX(260);
        imageView.setLayoutY(45);


        imageView.setPreserveRatio(true);



        anchorPane.getChildren().add(imageView);
        AnchorPane.setTopAnchor(button, (double) 630);
        AnchorPane.setRightAnchor(button, (double) 557);
        AnchorPane.setBottomAnchor(button, (double) 45);

        AnchorPane.setBottomAnchor(pane, (double) 0);
        AnchorPane.setTopAnchor(pane, (double) 0);
        AnchorPane.setRightAnchor(pane, (double) 0);
        AnchorPane.setLeftAnchor(pane, (double) 0);

        AnchorPane.setRightAnchor(imageView, (double) 260);
        AnchorPane.setBottomAnchor(imageView,(double) 135);
        AnchorPane.setTopAnchor(imageView,(double) 45);


        Holder.getChildren().add(anchorPane);

        changeViewImage();


    }

    private void loadImg(String url)
    {
        imageView.setImage(new Image(url));
        centerImage();
    }

    private void centerImage() {
        Image img = imageView.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reducCoeff = 0;
            if(ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            imageView.setX((imageView.getFitWidth() - w) / 2);
            imageView.setY((imageView.getFitHeight() - h) / 2);

        }
    }


}

