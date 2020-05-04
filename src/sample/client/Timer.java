package sample.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class Timer {
    private Label labelUpdate;
    private int[] sec4;
    private double[] step;
    private int[] allStep;

    public Timer(Label labelUpdate, int sec)
    {
        this.labelUpdate = labelUpdate;
        this.sec4 = new int[1];
        this.step = new double[1];
        this.allStep = new int[1];
        this.sec4[0] = sec * 10;
        this.step[0] = 1 / ((double)sec * 10);
        this.allStep[0] = sec * 10;
        init();
    }

    private void init() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100),event -> {
            sec4[0]--;
            labelUpdate.setOpacity(labelUpdate.getOpacity() - step[0]);
        }));
        timeline.setCycleCount(allStep[0]);
        timeline.play();
    }


}
