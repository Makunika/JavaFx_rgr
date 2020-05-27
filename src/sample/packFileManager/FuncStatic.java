package sample.packFileManager;

import com.jfoenix.controls.JFXProgressBar;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sample.client.DataClient;

public class FuncStatic {

    private static Label labelStorage;
    private static JFXProgressBar progressBar;

    public static void initStorage(Label labelStorage, JFXProgressBar progressBar) {
        FuncStatic.labelStorage = labelStorage;
        FuncStatic.progressBar = progressBar;
    }

    public static void updateStorage() {
        double ratiox = (double) DataClient.storageFill / (double)DataClient.storageAll;
        FuncStatic.progressBar.setProgress(ratiox);
        FuncStatic.labelStorage.setText(FuncStatic.getStringStorage(DataClient.storageFill) + " / " + FuncStatic.getStringStorage(DataClient.storageAll));
    }

    public static String getStringStorage(long storage)
    {
        int i = 0;
        while (storage > 1023)
        {
            i++;
            if (i == 4) break;
            storage /= 1024;
        }
        String str = Long.toString(storage);
        switch (i)
        {
            case 0:
            {
                str += " Байт";
                break;
            }
            case 1:
            {
                str += " Кб";
                break;
            }
            case 2:
            {
                str += " Мб";
                break;
            }
            case 3:
            {
                str += " Гб";
                break;
            }
            default: break;
        }
        return str;
    }


    public static void loadCustomPicter(ImageView imageView)
    {
        //imageView.setPreserveRatio(true);


        //imageView.setSmooth(true);
        imageView.setImage(new Image("custom.u"));
    }
}
