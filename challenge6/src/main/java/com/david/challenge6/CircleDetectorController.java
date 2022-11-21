package com.david.challenge6;

import com.github.sarxos.webcam.Webcam;
import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.util.Arrays;
import java.util.Optional;

public class CircleDetectorController {
    @FXML
    private ImageView originalImageView, greyscaleImageView, edgeImageView, thresholdImageView, circleImageView;
    private Image originalImage, greyscaleImage, edgeImage, thresholdImage, circleImage;
    private final Webcam webcam;

    private Circle prevCircle;

    public CircleDetectorController() {
        webcam = Webcam.getWebcams().get(0);
        webcam.open();
        new AnimationTimer() {
            private int numConsecutive = 0;

            @Override
            public void handle(long now) {
                originalImage = SwingFXUtils.toFXImage(webcam.getImage(), null);
                greyscaleImage = createGreyscaleImage(originalImage);
                edgeImage = createEdgeImage(greyscaleImage);
                thresholdImage = createThresholdImage(edgeImage);

                numConsecutive = prevCircle == null ? 0 : numConsecutive + 1;
                if (numConsecutive > 22) {
                    numConsecutive = 0;
                    prevCircle = null;
                }

                Optional<Circle> circle = findCircle(thresholdImage, prevCircle);
                circleImage = circle.map(c -> createCircleImage(originalImage, new Circle(c.x + 1, c.y + 1, c.r)))
                        .orElse(originalImage);
                prevCircle = circle.orElse(null);

                originalImageView.setImage(originalImage);
                greyscaleImageView.setImage(greyscaleImage);
                edgeImageView.setImage(edgeImage);
                thresholdImageView.setImage(thresholdImage);
                circleImageView.setImage(circleImage);
            }
        }.start();
    }

    private Image createGreyscaleImage(Image originalImage) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        byte[] buffer = new byte[width * height * 4];

        originalImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);

        for (int i = 0; i < buffer.length; i += 4) {
            float col = (buffer[i] & 0xFF) * .229f + (buffer[i + 1] & 0xFF) * .587f + (buffer[i + 2] & 0xFF) * .114f;
            buffer[i] = buffer[i + 1] = buffer[i + 2] = (byte) col;
        }
        return new WritableImage(width, height) {{
            getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                    buffer, 0, width * 4);
        }};
    }

    private Image createEdgeImage(Image greyscaleImage) {
        int greyscaleWidth = (int) greyscaleImage.getWidth();
        int greyscaleHeight = (int) greyscaleImage.getHeight();
        byte[] greyscaleBuffer = new byte[greyscaleWidth * greyscaleHeight * 4];

        greyscaleImage.getPixelReader().getPixels(0, 0, greyscaleWidth, greyscaleHeight,
                PixelFormat.getByteBgraInstance(), greyscaleBuffer, 0, greyscaleWidth * 4);

        int[][] pixels = new int[greyscaleHeight][greyscaleWidth];
        for (int i = 0; i < greyscaleBuffer.length / 4; i++) {
            pixels[i / greyscaleWidth][i % greyscaleWidth] = greyscaleBuffer[i * 4] & 0xFF;
        }

        int width = greyscaleWidth - 2;
        int height = greyscaleHeight - 2;
        byte[] buffer = new byte[width * height * 4];

        int[][] kernelX = new int[][] {{ 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 }};
        int[][] kernelY = new int[][] {{ 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 }};

        double gMax = -1;
        double[] gList = new double[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gx = kernelX[0][0] * pixels[y][x] + kernelX[0][1] * pixels[y][x + 1] + kernelX[0][2] * pixels[y][x + 2] +
                        kernelX[1][0] * pixels[y + 1][x] + kernelX[1][1] * pixels[y + 1][x + 1] + kernelX[1][2] * pixels[y + 1][x + 2] +
                        kernelX[2][0] * pixels[y + 2][x] + kernelX[2][1] * pixels[y + 2][x + 1] + kernelX[2][2] * pixels[y + 2][x + 2];
                int gy = kernelY[0][0] * pixels[y][x] + kernelY[0][1] * pixels[y][x + 1] + kernelY[0][2] * pixels[y][x + 2] +
                        kernelY[1][0] * pixels[y + 1][x] + kernelY[1][1] * pixels[y + 1][x + 1] + kernelY[1][2] * pixels[y + 1][x + 2] +
                        kernelY[2][0] * pixels[y + 2][x] + kernelY[2][1] * pixels[y + 2][x + 1] + kernelY[2][2] * pixels[y + 2][x + 2];

                double g = Math.sqrt(gx * gx + gy * gy);
                if (g > gMax) gMax = g;

                gList[y * width + x] = g;
            }
        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int index = j * width * 4 + i * 4;
                buffer[index] = buffer[index + 1] = buffer[index + 2] = (byte) (0xFF * gList[j * width + i] / gMax);
                buffer[index + 3] = (byte) 0xFF;
            }
        }

        return new WritableImage(width, height) {{
            getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                    buffer, 0, width * 4);
        }};
    }

    private Image createThresholdImage(Image edgeImage) {
        int width = (int) edgeImage.getWidth();
        int height = (int) edgeImage.getHeight();
        byte[] buffer = new byte[width * height * 4];

        edgeImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);

        final int threshold = 69;

        for (int i = 0; i < buffer.length; i++) {
            if ((buffer[i] & 0xFF) < threshold) buffer[i] = 0;
        }
        return new WritableImage(width, height) {{
            getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                    buffer, 0, width * 4);
        }};
    }

    private Optional<Circle> findCircle(Image thresholdImage, Circle prevCircle) {
        int width = (int) thresholdImage.getWidth();
        int height = (int) thresholdImage.getHeight();
        byte[] buffer = new byte[width * height * 4];

        thresholdImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);

        int[][] pixels = new int[height][width];
        for (int i = 0; i < buffer.length / 4; i++) {
            pixels[i / width][i % width] = buffer[i * 4] & 0xFF;
        }

        int rMin = Math.max(width, height) / 20, rMax = Math.min(width, height) / 2,
                xMin = 0, xMax = width - 1, yMin = 0, yMax = height - 1;
        if (prevCircle != null) {
            rMin = Math.max(rMin, prevCircle.r - 6);
            rMax = Math.min(rMax, prevCircle.r + 6);
            xMin = Math.max(xMin, prevCircle.x - rMax - 15);
            xMax = Math.min(xMax, prevCircle.x + rMax + 15);
            yMin = Math.max(yMin, prevCircle.y - rMax - 15);
            yMax = Math.min(yMax, prevCircle.y + rMax + 15);
        }

        int[][][] acc = new int[width][height][rMax - rMin];
        Arrays.stream(acc).forEach(a -> Arrays.stream(a).forEach(b -> Arrays.fill(b, 0)));

        final int divisions = 50;

        float[] sinMap = new float[divisions], cosMap = new float[divisions];
        for (int angleStep = 0; angleStep < divisions; angleStep++) {
            sinMap[angleStep] = (float) Math.sin(Math.TAU * angleStep / (float) divisions);
            cosMap[angleStep] = (float) Math.cos(Math.TAU * angleStep / (float) divisions);
        }

        for (int r = rMin; r < rMax; r++) {
            for (int y = yMin; y < yMax; y++) {
                for (int x = xMin; x < xMax; x++) {
                    if (pixels[y][x] > 0) {
                        for (int angleStep = 0; angleStep < divisions; angleStep++) {
                            int b = (int) (y - r * sinMap[angleStep]);
                            int a = (int) (x - r * cosMap[angleStep]);
                            if (a >= 0 && b >= 0 && a < width && b < height) acc[a][b][r - rMin]++;
                        }
                    }
                }
            }
        }

        int maxVal = 0;
        Circle circle = new Circle(0, 0, 0);
        for (int r = rMin; r < rMax; r++) {
            for (int y = yMin; y < yMax; y++) {
                for (int x = xMin; x < xMax; x++) {
                    if (acc[x][y][r - rMin] > maxVal) {
                        maxVal = acc[x][y][r - rMin];
                        circle.x = x;
                        circle.y = y;
                        circle.r = r;
                    }
                }
            }
        }

        return maxVal < (prevCircle == null ? 25 : 15) ? Optional.empty() : Optional.of(circle);
    }

    private Image createCircleImage(Image originalImage, Circle circle) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        byte[] buffer = new byte[width * height * 4];

        originalImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (Math.abs(Math.sqrt((i - circle.x) * (i - circle.x) +
                        (j - circle.y) * (j - circle.y)) -circle.r) < 2) {
                    int index = j * width * 4 + i * 4;
                    buffer[index + 2] = (byte) 0xFF;
                    buffer[index + 1] = (byte) 0x0;
                    buffer[index] = (byte) 0x0;
                }
            }
        }

        return new WritableImage(width, height) {{
            getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                    buffer, 0, width * 4);
        }};
    }
}