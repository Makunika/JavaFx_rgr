package sample.packFileManager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.regex.Pattern;

public class DataFile {
    private ObjectProperty<ImageView> icon;
    private StringProperty name;
    private StringProperty date;
    private StringProperty size;

    public DataFile(String data) throws IOException {
        icon = new SimpleObjectProperty<>();
        name = new SimpleStringProperty();
        date = new SimpleStringProperty();
        size = new SimpleStringProperty();
        parseString(data);
    }

    public void setData(String data) throws IOException {
        parseString(data);
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

    private void parseString(String data) throws IOException {
        Pattern pattern = Pattern.compile(":");
        String[] strings = pattern.split(data);
        if (strings.length == 4)
        {
            setIcon(new ImageView(new Image(strings[0].equals("file") ? "sample/packFileManager/123.jpg" : "sample/packFileManager/14124.png")));
            setName(strings[1]);
            setDate(strings[2]);
            setSize(strings[3]);
            getIcon().setFitWidth(25);
            getIcon().setFitHeight(25);
        }
        else
        {
            throw new IOException("length not 3");
        }
    }

    @Override
    public String toString() {
        return name.getValue();
    }
}
