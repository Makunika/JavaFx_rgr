package sample.packFileManager;

import javafx.scene.shape.SVGPath;
import sample.client.SVGIcons;

public class SVGIconsLoaded {
    private SVGPath[] folder;
    private SVGPath[] media;
    private SVGPath[] music;
    private SVGPath[] image;
    private SVGPath[] file;
    private int iF = 0;
    private int iM = 0;
    private int iI = 0;
    private int iFile = 0;
    private int iMusic = 0;


    private SVGIconsLoaded()
    {
        folder = new SVGPath[100];
        media = new SVGPath[100];
        music = new SVGPath[100];
        file = new SVGPath[100];
        image = new SVGPath[100];
        for (int i = 0; i < folder.length; i++)
        {
            folder[i] = new SVGPath();
            folder[i].setContent(SVGIcons.FOLDER.getPath());
            folder[i].setStyle("-fx-fill:  #79a6f2");
            media[i] = new SVGPath();
            media[i].setContent(SVGIcons.VIDEO_CAM.getPath());
            media[i].setStyle("-fx-fill:  #79a6f2");
            image[i] = new SVGPath();
            image[i].setContent(SVGIcons.IMAGE.getPath());
            image[i].setStyle("-fx-fill:  #79a6f2");
            file[i] = new SVGPath();
            file[i].setContent(SVGIcons.DESCRIPTOR.getPath());
            file[i].setStyle("-fx-fill:  #79a6f2");
        }
        //TODO: MUSIC
    }

    private static SVGIconsLoaded svgIconsLoaded;

    public static void init(){
        svgIconsLoaded = new SVGIconsLoaded();
    }

    public static SVGIconsLoaded getInstance()
    {
        return svgIconsLoaded;
    }


    public SVGPath getFile() {
        iFile++;
        if (iFile >= file.length) iFile = 0;
        return file[iFile];
    }

    public SVGPath getFolder() {
        iF++;
        if (iF >= folder.length) iF = 0;
        return folder[iF];
    }

    public SVGPath getImage() {
        iI++;
        if (iI >= image.length) iI = 0;
        return image[iI];
    }

    public SVGPath getMedia() {
        iM++;
        if (iM >= media.length) iM = 0;
        return media[iM];
    }

    public SVGPath getMusic() {
        iMusic++;
        if (iMusic >= music.length) iMusic = 0;
        return music[iMusic];
    }
}
