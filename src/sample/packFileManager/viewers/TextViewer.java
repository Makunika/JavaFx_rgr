package sample.packFileManager.viewers;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.skins.JFXTextAreaSkin;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class TextViewer extends Viewer {

    private JFXTextArea textArea;

    public TextViewer(StackPane Holder) {
        super(Holder);
        setSize(950,540);
    }

    @Override
    protected Node getBody() {
        textArea = new JFXTextArea();
        textArea.setEditable(false);
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
