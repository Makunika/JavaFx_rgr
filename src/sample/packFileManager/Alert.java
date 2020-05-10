package sample.packFileManager;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import sample.client.SVGIcons;

public class Alert {
    private JFXDialog dialog;
    private JFXDialogLayout content;

    public Alert(StackPane stackPane, String alertMessage)
    {
        dialog = new JFXDialog();
        content = new JFXDialogLayout();
        SVGPath warnIcon = new SVGPath();
        warnIcon.setContent(SVGIcons.ERROR.getPath());
        warnIcon.setStyle("-fx-fill: #ff4632");
        content.setHeading(warnIcon);
        JFXButton exit = new JFXButton("Закрыть");
        exit.setStyle("-fx-text-fill: #cf3829; -fx-font-size: 16px");
        content.setActions(exit);
        exit.setOnAction(event -> {
            dialog.close();
        });
        Label label = new Label(alertMessage);
        label.setStyle("-fx-text-fill: #cf3829");
        content.setBody(label);
        dialog.setContent(content);
        dialog.setDialogContainer(stackPane);
    }

    public Alert(StackPane stackPane)
    {
        this(stackPane, "lost connection");
    }

    public void show()
    {
        dialog.show();
    }
}
