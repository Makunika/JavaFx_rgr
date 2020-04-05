package sample.packFileManager;

import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.util.regex.Pattern;

public class DataFile {
    private SimpleStringProperty icon1;
    private SimpleStringProperty name;
    private SimpleStringProperty date;
    private SimpleStringProperty size;

    public DataFile(String data) throws IOException {
        icon1 = new SimpleStringProperty();
        name = new SimpleStringProperty();
        date = new SimpleStringProperty();
        size = new SimpleStringProperty();
        parseString(data);
    }

    public void setData(String data) throws IOException {
        parseString(data);
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


    private void parseString(String data) throws IOException {
        Pattern pattern = Pattern.compile(":");
        String[] strings = pattern.split(data);
        if (strings.length == 4)
        {
            icon1.set(strings[0]);
            setName(strings[1]);
            setDate(strings[2]);
            setSize(strings[3]);
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
