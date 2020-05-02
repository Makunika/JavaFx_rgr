package sample.packFileManager;

import javafx.beans.property.*;

import java.util.regex.Pattern;

public class DataFile {
    //private ObjectProperty<ImageView> icon;
    private StringProperty icon;
    private StringProperty name;
    private StringProperty date;
    private StringProperty size;
    private boolean isFile;
    private boolean isPng;
    private String suffix;

    public DataFile(String type, String name, String date, String size) {
        //this.icon = new SimpleObjectProperty<>();
        this.icon = new SimpleStringProperty();
        //setIcon(new ImageView(new Image(type.equals("file") ? "sample/packFileManager/123.jpg" : "sample/packFileManager/14124.png")));
        this.name = new SimpleStringProperty();
        this.date = new SimpleStringProperty();
        this.size = new SimpleStringProperty();
        //this.icon.getValue().setFitHeight(25);
        //this.icon.getValue().setFitWidth(25);
        setData(type, name, date, size);
    }

    public void setData(String type, String name, String date, String size) {
        //setIcon(new ImageView(new Image(type.equals("file") ? "sample/packFileManager/123.jpg" : "sample/packFileManager/14124.png")));
        setIcon(type);
        isFile = type.equals("file");
        setName(name);
        setDate(date);
        setSize(size);
        isPng = false;
        suffix = ".file";

        if (isFile) {
            Pattern pattern = Pattern.compile("\\.");
            String[] strings = pattern.split(name);
            String[] formatPicter = new String[]{
                ".png",
                ".jpg",
                ".bmp"
            };
            suffix = "." + strings[strings.length-1];
            for (String format: formatPicter) {
                if (format.equals(suffix)) {
                    isPng = true;
                    break;
                }

            }

        }

    }

    public String getSuffix() {
        return suffix;
    }

    public boolean isPng() {
        return isPng;
    }

    public boolean isFile() {
        return isFile;
    }


    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty iconProperty() {
        return icon;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public String getIcon() {
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

    public void setIcon(String icon) {
        this.icon.set(icon);
    }


    @Override
    public String toString() {
        return name.getValue();
    }
}
