package sample.packFileManager.viewers;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class TextViewer extends Viewer {

    private TextArea textArea;

    public TextViewer(StackPane Holder) {
        super(Holder);
        setSize(950,540);
    }

    @Override
    protected Node getBody() {
        textArea = new TextArea();
        textArea.setEditable(false);

        URL url = this.getClass().getResource("/sample/resources/css/my-text-area.css");
        String css = url.toExternalForm();
        textArea.getStylesheets().add(css);


        return textArea;
    }

    @Override
    protected void delete() {
        textArea.setText("");
    }

    @Override
    public void loadBody() {
        try {
            textArea.setText(new String(Files.readAllBytes(file.toPath())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
