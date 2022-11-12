package com.david.spirograph;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpirographController {
    @FXML
    private Canvas canvas;

    private final int timeStep;

    public SpirographController() {
        timeStep = 20;
    }

    @FXML
    protected void draw() {
        GraphicsContext context = canvas.getGraphicsContext2D();
        float centreX = (float) (canvas.getWidth() * .5f);
        float centreY = (float) (canvas.getHeight() * .5f);
        context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        context.setStroke(Color.RED);

        float t = 0;
        for (int i = 0; i < 10000; ++i) {
            float r = 134, R = 81, O = 99, t2 = t + timeStep * .001f;
            float x = (float) ((R - r) * Math.cos(t) + O * Math.cos(((R - r) / r) * t));
            float y = (float) ((R - r) * Math.sin(t) - O * Math.sin(((R - r) / r) * t));
            float x2 = (float) ((R - r) * Math.cos(t2) + O * Math.cos(((R - r) / r) * t2));
            float y2 = (float) ((R - r) * Math.sin(t2) - O * Math.sin(((R - r) / r) * t2));
            context.strokeLine(x + centreX, y + centreY, x2 + centreX, y2 + centreY);
            t = t2;
        }
    }
}