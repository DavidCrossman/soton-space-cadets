package com.david.spirograph;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class SpirographController implements Initializable {
    @FXML
    private Button drawButton, stopButton;
    @FXML
    private Text textOffset, textInnerRadius, textOuterRadius, textSpeed, textTimeStep;
    @FXML
    private Slider sliderOffset, sliderInnerRadius, sliderOuterRadius, sliderSpeed, sliderTimeStep;
    @FXML
    private Canvas canvas;

    private long startTime;
    private double lastUpdateTime;
    private double timer;
    private boolean firstFrame;
    private final AnimationTimer animation;

    public SpirographController() {
        lastUpdateTime = 0;
        startTime = 0;
        firstFrame = true;
        timer = 0;

        animation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (firstFrame) {
                    startTime = now;
                    firstFrame = false;
                }
                now -= startTime;

                timer += now * .000000001 - lastUpdateTime;

                double t = lastUpdateTime;
                double timeStep = Math.pow(2, sliderTimeStep.getValue()) * .000001;
                double speed = Math.pow(2, sliderSpeed.getValue());

                while (timer > timeStep) {
                    timer -= timeStep;
                    t += timeStep;

                    GraphicsContext context = canvas.getGraphicsContext2D();
                    double centreX = canvas.getWidth() * .5;
                    double centreY = canvas.getHeight() * .5;

                    double innerRadius = (int) sliderInnerRadius.getValue();
                    double outerRadius = (int) sliderOuterRadius.getValue();
                    double offset = (int) sliderOffset.getValue();
                    double x = (outerRadius - innerRadius) * Math.cos(t * speed) +
                            offset * Math.cos(((outerRadius - innerRadius) / innerRadius) * t * speed);
                    double y = (outerRadius - innerRadius) * Math.sin(t * speed) -
                            offset * Math.sin(((outerRadius - innerRadius) / innerRadius) * t * speed);
                    double lastX = (outerRadius - innerRadius) * Math.cos(lastUpdateTime * speed) +
                            offset * Math.cos(((outerRadius - innerRadius) / innerRadius) * lastUpdateTime * speed);
                    double lastY = (outerRadius - innerRadius) * Math.sin(lastUpdateTime * speed) -
                            offset * Math.sin(((outerRadius - innerRadius) / innerRadius) * lastUpdateTime * speed);

                    context.setStroke(Color.RED);
                    context.strokeLine(lastX + centreX, lastY + centreY, x + centreX, y + centreY);

                    lastUpdateTime = t;
                }
            }
        };
    }

    @FXML
    protected void draw() {
        sliderOffset.setDisable(true);
        sliderInnerRadius.setDisable(true);
        sliderOuterRadius.setDisable(true);
        drawButton.setDisable(true);
        stopButton.setDisable(false);
        sliderSpeed.setDisable(true);
        sliderTimeStep.setDisable(true);

        firstFrame = true;
        lastUpdateTime = 0;
        timer = 0;
        animation.start();
    }

    @FXML
    protected void stop() {
        sliderOffset.setDisable(false);
        sliderInnerRadius.setDisable(false);
        sliderOuterRadius.setDisable(false);
        drawButton.setDisable(false);
        stopButton.setDisable(true);
        sliderSpeed.setDisable(false);
        sliderTimeStep.setDisable(false);

        animation.stop();
    }

    @FXML
    protected void clear() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textOffset.setText(String.valueOf((int) sliderOffset.getValue()));
        textInnerRadius.setText(String.valueOf((int) sliderInnerRadius.getValue()));
        textOuterRadius.setText(String.valueOf((int) sliderOuterRadius.getValue()));
        textSpeed.setText("%.2f".formatted(sliderSpeed.getValue() + 1));
        textTimeStep.setText("%.2f".formatted(sliderTimeStep.getValue() + 1));
        sliderOffset.valueProperty().addListener((observableValue, oldValue, newValue) ->
                textOffset.textProperty().setValue(String.valueOf(newValue.intValue())));
        sliderOuterRadius.valueProperty().addListener((observableValue, oldValue, newValue) ->
                textOuterRadius.textProperty().setValue(String.valueOf(newValue.intValue())));
        sliderInnerRadius.valueProperty().addListener((observableValue, oldValue, newValue) ->
                textInnerRadius.textProperty().setValue(String.valueOf(newValue.intValue())));
        sliderSpeed.valueProperty().addListener((observableValue, oldValue, newValue) ->
                textSpeed.textProperty().setValue("%.2f".formatted(newValue.doubleValue() + 1)));
        sliderTimeStep.valueProperty().addListener((observableValue, oldValue, newValue) ->
                textTimeStep.textProperty().setValue("%.2f".formatted(newValue.doubleValue() + 1)));
        stopButton.setDisable(true);
    }
}