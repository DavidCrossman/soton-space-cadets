package com.david.challenge6;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.*;

import java.net.URL;
import java.util.ResourceBundle;

public class CircleDetectorController implements Initializable {
    @FXML
    private ImageView originalImageView, greyscaleImageView, edgeImageView;

    private final Image originalImage, greyscaleImage, edgeImage;

    public CircleDetectorController() {
        originalImage = new Image("file:src/main/resources/com/david/challenge6/test3.png");
        greyscaleImage = createGreyscaleImage(originalImage);
        edgeImage = createEdgeImage(greyscaleImage);
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

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                int gx = kernelX[0][0] * pixels[j][i] + kernelX[0][1] * pixels[j][i + 1] + kernelX[0][2] * pixels[j][i + 2] +
                        kernelX[1][0] * pixels[j + 1][i] + kernelX[1][1] * pixels[j + 1][i + 1] + kernelX[1][2] * pixels[j + 1][i + 2] +
                        kernelX[2][0] * pixels[j + 2][i] + kernelX[2][1] * pixels[j + 2][i + 1] + kernelX[2][2] * pixels[j + 2][i + 2];
                int gy = kernelY[0][0] * pixels[j][i] + kernelY[0][1] * pixels[j][i + 1] + kernelY[0][2] * pixels[j][i + 2] +
                        kernelY[1][0] * pixels[j + 1][i] + kernelY[1][1] * pixels[j + 1][i + 1] + kernelY[1][2] * pixels[j + 1][i + 2] +
                        kernelY[2][0] * pixels[j + 2][i] + kernelY[2][1] * pixels[j + 2][i + 1] + kernelY[2][2] * pixels[j + 2][i + 2];

                double g = Math.sqrt(gx * gx + gy * gy);
                if (g > gMax) gMax = g;

                gList[j * width + i] = g;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        originalImageView.setImage(originalImage);
        greyscaleImageView.setImage(greyscaleImage);
        edgeImageView.setImage(edgeImage);
    }
}