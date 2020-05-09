package sample.packFileManager.viewers;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.net.MalformedURLException;

public class MediaViewer extends Viewer {
    private MediaView mediaView;

    public MediaViewer(StackPane Holder) {
        super(Holder);
        setSize(900,550);
    }

    @Override
    protected Node getBody() {
        mediaView = new MediaView();
        mediaView.setFitHeight(550);
        mediaView.setFitWidth(900);
        mediaView.setPreserveRatio(true);
        return mediaView;
    }

    @Override
    protected void delete() {
        MediaPlayer mp = mediaView.getMediaPlayer();
        mp.stop();
        mp.dispose();
        mp = null;
        mediaView.setMediaPlayer(null);
        System.gc();
    }

    @Override
    public void loadBody() {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(file.toURI().toURL().toString()));
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
