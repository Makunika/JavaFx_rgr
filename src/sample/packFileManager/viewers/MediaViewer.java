package sample.packFileManager.viewers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.StringConverter;
import sample.client.SVGIcons;
import sample.packFileManager.MySVGPath;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class MediaViewer extends Viewer {
    private MediaView mediaView;
    private JFXSlider sliderTime;
    private JFXSlider sliderVolume;
    private MediaPlayer mediaPlayer;
    boolean pause = false;

    public MediaViewer(StackPane Holder) {
        super(Holder);
        //setSize(900,550);
        setMaxHeight(500);
    }

    @Override
    protected Node getBody() {
        mediaView = new MediaView();
        //mediaView.setFitHeight(550);
        mediaView.setFitWidth(890);
        mediaView.setPreserveRatio(true);
        MySVGPath svgVolume = new MySVGPath(SVGIcons.VOLUME);
        svgVolume.setStyle("-fx-fill: #90a4ae");
        FlowPane.setMargin(svgVolume, new Insets(0,0,19,15));
        loadSliderTime();
        loadSliderVolume();

        getContent().getActions().addAll(loadPlayPause(), sliderTime, svgVolume, sliderVolume);
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
            mediaPlayer = new MediaPlayer(new Media(file.toURI().toURL().toString()));
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setAutoPlay(true);

            mediaPlayer.currentTimeProperty().addListener((ov) -> {
                sliderTime.setValue(mediaPlayer.getCurrentTime().toSeconds());
            });

            mediaPlayer.setOnReady(() -> {
                double sec = mediaPlayer.getTotalDuration().toSeconds();
                sliderTime.setDisable(false);
                sliderTime.setMax(sec);
                sliderTime.setMin(0);
                sliderTime.setValue(0);
                sliderVolume.setMax(1);
                mediaPlayer.volumeProperty().bind(sliderVolume.valueProperty());
                //sliderVolume.valueProperty().bind(mediaPlayer.volumeProperty());
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void loadSliderTime()
    {
        sliderTime = new JFXSlider();
        sliderTime.setMinWidth(300);
        //FlowPane.setMargin(sliderTime, new Insets(0,0,0,0));
        sliderTime.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                int sec = (int)Math.round(object) % 60;
                return (int)Math.round(object) / 60 + ":" +  (sec > 10? sec : "0" + sec);
            }

            @Override
            public Double fromString(String string) {
                Pattern pattern = Pattern.compile(":");
                String[] strings = pattern.split(string);
                return Double.parseDouble(strings[0]) * 60 + Double.parseDouble(strings[1]);
            }
        });
        sliderTime.valueProperty().addListener((ov) -> {
            if (sliderTime.isValueChanging())
            {
                mediaPlayer.seek(new Duration(sliderTime.getValue() * 1000));
            }
        });
        sliderTime.setOnMouseClicked(event -> {
            mediaPlayer.seek(new Duration(sliderTime.getValue() * 1000));
        });

        sliderTime.setOnMouseEntered(event -> {
            if (!pause) mediaPlayer.pause();
        });

        sliderTime.setOnMouseExited(event -> {
            if (!pause) mediaPlayer.play();
        });

        sliderTime.setShowTickLabels(true);
        URL url = this.getClass().getResource("/sample/resources/css/my-slider.css");
        String css = url.toExternalForm();
        sliderTime.getStylesheets().add(css);
    }

    private JFXButton loadPlayPause() {
        MySVGPath svgPathPause = new MySVGPath(SVGIcons.PAUSE);
        svgPathPause.setStyle("-fx-fill: #90a4ae");
        MySVGPath svgPathPlay = new MySVGPath(SVGIcons.PLAY);
        svgPathPlay.setStyle("-fx-fill: #90a4ae");


        JFXButton playPause = new JFXButton("", svgPathPause);

        playPause.setStyle("-fx-font-size: 20px;");
        playPause.setRipplerFill(Color.web("#81d4fa"));


        FlowPane.setMargin(playPause,new Insets(0,0,19,10));


        playPause.setOnMouseClicked(event -> {
            if (!pause) {
                mediaPlayer.pause();
                playPause.setGraphic(svgPathPlay);
                pause = true;
            } else {
                mediaPlayer.play();
                playPause.setGraphic(svgPathPause);
                pause = false;
            }
        });
        return playPause;
    }

    private void loadSliderVolume()
    {
        sliderVolume = new JFXSlider();
        FlowPane.setMargin(sliderVolume, new Insets(0,315,0,0));
        sliderVolume.setMinWidth(100);
        sliderVolume.setShowTickLabels(true);
        sliderVolume.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return Integer.toString((int)Math.round(object * 100));
            }

            @Override
            public Double fromString(String string) {
                return Double.parseDouble(string) / 100;
            }
        });

        URL url = this.getClass().getResource("/sample/resources/css/my-slider.css");
        String css = url.toExternalForm();
        sliderVolume.getStylesheets().add(css);
    }
}
