package sample.packFileManager.viewers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import sample.client.SVGIcons;

import java.io.File;
import java.io.IOException;

public abstract class Viewer {
    private final StackPane refHolder;
    private JFXDialog dialog;
    protected File file;
    private JFXDialogLayout content;


    public Viewer(StackPane Holder) {
        refHolder = Holder;
        load();
    }

    private void load() {
        content = new JFXDialogLayout();
        dialog = new JFXDialog(refHolder, content, JFXDialog.DialogTransition.CENTER);
        content.setHeading(new Label("Просмотр"));
        content.setBody(getBody());
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(SVGIcons.CLEAR.getPath());
        svgPath.setFill(Color.web("#90a4ae"));
        JFXButton jfxButton = new JFXButton("",svgPath);
        jfxButton.setButtonType(JFXButton.ButtonType.FLAT);
        jfxButton.setStyle("-fx-font-size: 20px");
        jfxButton.setTextFill(Color.web("#79a6f2"));
        jfxButton.setRipplerFill(Color.web("#81d4fa"));
        jfxButton.setOnAction(event1 -> {
            dialog.close();
        });
        dialog.setOnDialogClosed(event -> {
            delete();
            file.delete();
        });
        content.getHeading().get(0).setStyle("-fx-font-weight: normal; -fx-text-fill: dimgray; -fx-opacity: 0.5");
        content.getActions().add(jfxButton);
    }

    protected abstract Node getBody();

    protected abstract void delete();

    public abstract void loadBody();

    protected void setBody(Node... body)
    {
        content.setBody(body);
    }

    public void dialogView()
    {
        dialog.show();
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

    public void addHeader(String nameFile)
    {
        ((Label) content.getHeading().get(0)).setText("Просмотр " + nameFile);
    }

    protected void setSize(double width, double height)
    {
        content.setPrefSize(width, height);
    }

    protected void setWidth(double width)
    {
        content.setPrefWidth(width);
    }

    protected void setHeight(double height)
    {
        content.setPrefHeight(height);
    }

    protected void setMaxHeight(double height)
    {
        content.setMaxHeight(height);
    }

    public JFXDialogLayout getContent() {
        return content;
    }
}
