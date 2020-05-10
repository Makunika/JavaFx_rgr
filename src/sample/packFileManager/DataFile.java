package sample.packFileManager;

import javafx.beans.property.*;
import javafx.scene.shape.SVGPath;

import java.util.regex.Pattern;

public class DataFile{
    //private ObjectProperty<ImageView> icon;
    private ObjectProperty<SVGPath> icon;
    private StringProperty name;
    private StringProperty date;
    private StringProperty size;
    private boolean isFile;
    private boolean isPng;
    private boolean isTxt;
    private boolean isMedia;
    private boolean isNone;
    private String suffix;

    public DataFile(String type, String name, String date, String size) {
        //this.icon = new SimpleObjectProperty<>();
        this.icon = new SimpleObjectProperty<>();
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
        isFile = type.equals("file");
        setName(name);
        setDate(date);
        setSize(size);
        isPng = false;
        isNone = false;
        isTxt = false;
        isMedia = false;
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
                    setIcon(SVGIconsLoaded.getInstance().getImage());
                    break;
                }

            }
            if (!isPng && suffix.equals(".txt"))
            {
                setIcon(SVGIconsLoaded.getInstance().getFile());
                isTxt = true;
            }
            else if (!isPng && suffix.equals(".mp4"))
            {
                setIcon(SVGIconsLoaded.getInstance().getMedia());
                isMedia = true;
            }
            else if (!isPng)
            {
                setIcon(SVGIconsLoaded.getInstance().getFile());
                isNone = true;
            }
        }
        else {
            setIcon(SVGIconsLoaded.getInstance().getFolder());
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

    public boolean isMedia() {
        return isMedia;
    }

    public boolean isNone() {
        return isNone;
    }

    public boolean isTxt() {
        return isTxt;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public ObjectProperty<SVGPath> iconProperty() {
        return icon;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public SVGPath getIcon() {
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

    public void setIcon(SVGPath icon) {
        this.icon.set(icon);
    }


    @Override
    public String toString() {
        return name.getValue();
    }
}
