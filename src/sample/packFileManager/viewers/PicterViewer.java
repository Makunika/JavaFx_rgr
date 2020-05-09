package sample.packFileManager.viewers;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class PicterViewer extends Viewer {
    private ImageView imageView;

    public PicterViewer(StackPane Holder)
    {
        super(Holder);
    }

    @Override
    protected Node getBody() {
        imageView = new ImageView();
        imageView.setFitWidth(860);
        imageView.setFitHeight(740);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    @Override
    protected void delete() {
        Image image = imageView.getImage();
        image = null;
        imageView.setImage(null);
        System.gc();
    }

    @Override
    public void loadBody() {
        String url = file.getAbsolutePath();

        imageView.setImage(new Image(file.toURI().toString()));
    }


    public void setImage(String url)
    {
        imageView.setImage(new Image(url));
    }

    public ImageView getImageView() {
        return imageView;
    }

}
