package sample.packFileManager;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;

public class PicterViewer {
    private StackPane refHolder;
    private ImageView imageView;
    private Pane pane;
    private Button button;
    private AnchorPane anchorPane;
    private StackPane stackPane;
    private File file;


    public PicterViewer(StackPane Holder)
    {
        refHolder = Holder;
        load();
    }



    private void load()
    {
        anchorPane = new AnchorPane();

        pane = new Pane();
        pane.setStyle("-fx-background-color:  #696969; -fx-opacity: 0.4");

        pane.setPrefSize(1280,720);
        pane.setLayoutX(0);
        pane.setLayoutY(0);
        anchorPane.getChildren().add(pane);

        button = new Button();
        button.setText("Back");
        button.setFont(Font.font(22));
        button.setPrefSize(166,40);
        button.setLayoutX(557);
        button.setLayoutY(630);
        button.setStyle("-fx-background-color: #578eff");
        button.setOnMouseClicked(event -> {
            file.delete();
            changeView();
        });
        //anchorPane.getChildren().add(button);


        imageView = new ImageView();
        imageView.setStyle("-fx-background-color: #ff0900");
        imageView.setFitWidth(760);
        imageView.setFitHeight(540);
        imageView.setPreserveRatio(true);
        //imageView.setLayoutX(260);
        //imageView.setLayoutY(45);

        stackPane = new StackPane();
        //stackPane.setPrefSize(760,540);
        stackPane.setPrefSize(760,540);

        stackPane.getChildren().add(imageView);
        stackPane.setAlignment(imageView, Pos.CENTER);

        anchorPane.getChildren().add(stackPane);
        anchorPane.getChildren().add(button);

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


        refHolder.getChildren().add(anchorPane);

        changeView();
    }

    public void setImage(String url)
    {
        imageView.setImage(new Image(url));
    }

    public void changeView()
    {
        ObservableList<Node> childs = refHolder.getChildren();
        if (childs.size() > 1) {
            //
            Node topNode = childs.get(childs.size()-1);
            topNode.toBack();
        }
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void loadImage()
    {
        String url = file.getAbsolutePath();

        imageView.setImage(new Image(file.toURI().toString()));
    }

    public File getTmpFile(String suffix)
    {
        try {
            if (file != null && file.exists())
                file.delete();
            file = File.createTempFile("Manager", suffix);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
