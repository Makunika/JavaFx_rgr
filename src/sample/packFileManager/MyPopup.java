package sample.packFileManager;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MyPopup {
    private final JFXPopup popup;

    public MyPopup(ObservableList<JFXButton> buttons, int width)
    {
        VBox vBox = new VBox();
        for (JFXButton button: buttons) {
            button.setPadding(new Insets(7));
            button.setOnMouseEntered(event -> {
                button.setBackground(new Background(new BackgroundFill(Color.rgb(168,172,250,0.46),
                        CornerRadii.EMPTY, Insets.EMPTY)));
            });
            button.setRipplerFill(Color.rgb(105,133,217,0.8));
            button.setPrefWidth(width);
            button.setFocusTraversable(false);
            button.setGraphicTextGap(15);
            button.setStyle("-fx-alignment: center-left;");
            button.setOnMouseExited(event -> {
                button.setBackground(new Background(new BackgroundFill(Color.rgb(143,147,255,0),
                        CornerRadii.EMPTY, Insets.EMPTY)));
            });
            vBox.getChildren().add(button);
        }
        popup = new JFXPopup();
        popup.setPopupContent(vBox);
    }

    public void show(Node node, double x, double y)
    {
        popup.show(node, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, x, y);
    }

    public void hide()
    {
        popup.hide();
    }

    public JFXButton get(int index)
    {
        return (JFXButton)((VBox)popup.getPopupContent()).getChildren().get(index);
    }
}
