package com.david.spirograph;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpirographController {
    @FXML
    private Canvas canvas;

    private final double timeStep;
    private long startTime;
    private double lastUpdateTime;
    private double timer;
    private final double speed;

    private boolean firstFrame;

    private final AnimationTimer animation;

    public SpirographController() {
        timeStep = 0.0001;
        lastUpdateTime = 0;
        startTime = 0;
        firstFrame = true;
        timer = 0;
        speed = 50;

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
                while (timer > timeStep) {
                    timer -= timeStep;
                    t += timeStep;

                    GraphicsContext context = canvas.getGraphicsContext2D();
                    double centreX = canvas.getWidth() * .5;
                    double centreY = canvas.getHeight() * .5;

                    double r = 134, R = 81, O = 99;
                    double x = (R - r) * Math.cos(t * speed) + O * Math.cos(((R - r) / r) * t * speed);
                    double y = (R - r) * Math.sin(t * speed) - O * Math.sin(((R - r) / r) * t * speed);
                    double lastX = (R - r) * Math.cos(lastUpdateTime * speed) + O * Math.cos(((R - r) / r) * lastUpdateTime * speed);
                    double lastY = (R - r) * Math.sin(lastUpdateTime * speed) - O * Math.sin(((R - r) / r) * lastUpdateTime * speed);

                    context.setStroke(Color.RED);
                    context.strokeLine(lastX + centreX, lastY + centreY, x + centreX, y + centreY);

                    lastUpdateTime = t;
                }
            }
        };
    }

    @FXML
    protected void draw() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        firstFrame = true;
        lastUpdateTime = 0;
        timer = 0;
        animation.start();
    }
}