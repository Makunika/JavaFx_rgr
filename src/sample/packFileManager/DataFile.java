package sample.packFileManager;

import javafx.beans.property.*;
import javafx.scene.shape.SVGPath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class DataFile implements Comparable<DataFile> {
    private final ObjectProperty<SVGPath> icon;
    private final StringProperty name;
    private final StringProperty date;
    private final StringProperty size;
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
        suffix = ".path";
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
                if (format.equalsIgnoreCase(suffix)) {
                    isPng = true;
                    setIcon(SVGIconsLoaded.getInstance().getImage());
                    break;
                }

            }
            if (!isPng && suffix.equalsIgnoreCase(".txt"))
            {
                setIcon(SVGIconsLoaded.getInstance().getTxt());
                isTxt = true;
            }
            else if (!isPng && (suffix.equalsIgnoreCase(".mp4") ||
                    suffix.equalsIgnoreCase(".mp3") ||
                    suffix.equalsIgnoreCase(".wav")))
            {
                if (suffix.equalsIgnoreCase(".mp3") ||
                        suffix.equalsIgnoreCase(".wav"))
                    setIcon(SVGIconsLoaded.getInstance().getMusic());
                else setIcon(SVGIconsLoaded.getInstance().getMedia());
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

        //date

        try {
            Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            setDate(dateFormat.format(date1));
        } catch (ParseException e) {
            e.printStackTrace();
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

    @Override
    public int compareTo(DataFile o) {
        return getName().compareTo(o.getName());
    }
}
