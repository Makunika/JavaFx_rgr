package sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataClient {
    public static String login;
    public static String password;
    public static boolean isAutoEnter;
    public static boolean isSavedPassword;
    public static String email;

    public static final String SERVER = "localhost";
    public static final int PORT_MESSAGE = 24571;
    public static final int PORT_FILE = 24570;


    public static void loadPreferences()
    {

    }



    public static void SavedPreferences()
    {
        try {
            File filePreferences = new File(new File(new File(".").getCanonicalPath()), "Preferences.txt");
            if (filePreferences.exists()) {
                if (filePreferences.canWrite()) {
                    try (BufferedWriter br = new BufferedWriter(new FileWriter(filePreferences))) {
                        br.write(DataClient.isSavedPassword + "\n"
                                + DataClient.isAutoEnter + "\n"
                                + DataClient.login + "\n"
                                + (DataClient.isSavedPassword ? DataClient.password : ""));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
