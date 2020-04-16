package sample.packFileManager;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.regex.Pattern;

public class DataFile {
    private ObjectProperty<ImageView> icon;
    private StringProperty name;
    private StringProperty date;
    private StringProperty size;
    private boolean isFile;

    public DataFile(String type, String name, String date, String size) {
        this.icon = new SimpleObjectProperty<>();
        setIcon(new ImageView(new Image(type.equals("file") ? "sample/packFileManager/123.jpg" : "sample/packFileManager/14124.png")));
        isFile = type.equals("file");

        this.name = new SimpleStringProperty();
        setName(name);
        this.date = new SimpleStringProperty();
        setDate(date);
        this.size = new SimpleStringProperty();
        setSize(size);
        this.icon.getValue().setFitHeight(25);
        this.icon.getValue().setFitWidth(25);
    }

    public void setData(String type, String name, String date, String size) {
        setIcon(new ImageView(new Image(type.equals("file") ? "sample/packFileManager/123.jpg" : "sample/packFileManager/14124.png")));
        isFile = type.equals("file");
        setName(name);
        setDate(date);
        setSize(size);
    }

    public boolean isFile() {
        return isFile;
    }


    public StringProperty dateProperty() {
        return date;
    }

    public ObjectProperty<ImageView> iconProperty() {
        return icon;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public ImageView getIcon() {
        return icon.get();
    }

    public String getName() {
        return name.get();
    }

    public String getSize() {
        return size.get();
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public void setIcon(ImageView icon) {
        this.icon.set(icon);
    }


    @Override
    public String toString() {
        return name.getValue();
    }
}
