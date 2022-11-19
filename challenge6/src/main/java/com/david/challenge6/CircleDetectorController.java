package com.david.challenge6;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class CircleDetectorController implements Initializable {
    @FXML
    private ImageView originalImageView, greyscaleImageView, edgeImageView, thresholdImageView, circleImageView;

    private final Image originalImage, greyscaleImage, edgeImage, thresholdImage, circleImage;

    public CircleDetectorController() {
        originalImage = new Image("file:src/main/resources/com/david/challenge6/test1.png");
        greyscaleImage = createGreyscaleImage(originalImage);
        edgeImage = createEdgeImage(greyscaleImage);
        thresholdImage = createThresholdImage(edgeImage);
        Circle circle = findCircle(edgeImage);
        circle.x++;
        circle.y++;
        circleImage = createCircleImage(originalImage, circle);
    }

    private WritableImage createGreyscaleImage(Image originalImage) {
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

    private WritableImage createEdgeImage(Image greyscaleImage) {
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

    private WritableImage createThresholdImage(Image edgeImage) {
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

    private Circle findCircle(Image thresholdImage) {
        int width = (int) thresholdImage.getWidth();
        int height = (int) thresholdImage.getHeight();
        byte[] buffer = new byte[width * height * 4];

        thresholdImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);

        int[][] pixels = new int[height][width];
        for (int i = 0; i < buffer.length / 4; i++) {
            pixels[i / width][i % width] = buffer[i * 4] & 0xFF;
        }

        final int rMin = 30, rMax = Math.min(width, height) / 2;
        int[][][] acc = new int[width][height][rMax - rMin];
        Arrays.stream(acc).forEach(a -> Arrays.stream(a).forEach(b -> Arrays.fill(b, 0)));

        for (int r = rMin; r < rMax; r++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    for (float theta = 0; theta < Math.TAU; theta += .1f) {
                        int b = (int) (y - r * Math.sin(theta));
                        int a = (int) (x - r * Math.cos(theta));
                        if (a >= 0 && b >= 0 && a < width && b < height && pixels[y][x] > 60) acc[a][b][r - rMin]++;
                    }
                }
            }
        }

        int maxVal = 0, maxX = 0, maxY = 0, maxR = 0;
        for (int r = rMin; r < rMax; r++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (acc[x][y][r - rMin] > maxVal) {
                        maxVal = acc[x][y][r - rMin];
                        maxX = x;
                        maxY = y;
                        maxR = r;
                    }
                }
            }
        }

        return new Circle(maxX, maxY, maxR);
    }

    private WritableImage createCircleImage(Image originalImage, Circle circle) {
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        byte[] buffer = new byte[width * height * 4];

        originalImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (Math.abs((i - circle.x) * (i - circle.x) +
                        (j - circle.y) * (j - circle.y) - circle.r * circle.r) < 200) {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        originalImageView.setImage(originalImage);
        greyscaleImageView.setImage(greyscaleImage);
        edgeImageView.setImage(edgeImage);
        thresholdImageView.setImage(thresholdImage);
        circleImageView.setImage(circleImage);
    }
}