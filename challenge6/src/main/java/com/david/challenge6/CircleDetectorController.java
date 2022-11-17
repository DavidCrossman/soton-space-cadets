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

        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();
        byte[] buffer = new byte[width * height * 4];

        originalImage.getPixelReader().getPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(),
                buffer, 0, width * 4);

        for (int i = 0; i < buffer.length; i += 4) {
            float col = (buffer[i] & 0xFF) * .229f + (buffer[i + 1] & 0xFF) * .587f + (buffer[i + 2] & 0xFF) * .114f;
            buffer[i] = buffer[i + 1] = buffer[i + 2] = (byte) col;
        }

        greyscaleImage = new WritableImage(width, height) {{
            PixelWriter writer = getPixelWriter();
            writer.setPixels(0, 0, width, height, PixelFormat.getByteBgraInstance(), buffer, 0, width * 4);
        }};

        byte[] buffer2 = new byte[(width - 2) * (height - 2) * 4];

        int[][] kernelX = new int[][] {{ 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 }};
        int[][] kernelY = new int[][] {{ 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 }};

        double maxG = 0;
        double[] gList = new double[(width - 1) * (height - 1)];

        for (int j = 1; j < height - 1; j++) {
            for (int i = 1; i < width - 1; i++) {
                int gx = kernelX[0][0] * (buffer[(j - 1) * width * 4 + i * 4 - 4] & 0xFF) + kernelX[0][1] * (buffer[(j - 1) * width * 4 + i * 4] & 0xFF) + kernelX[0][2] * (buffer[(j - 1) * width * 4 + i * 4 + 4] & 0xFF) +
                        kernelX[1][0] * (buffer[j * width * 4 + i * 4 - 4] & 0xFF) + kernelX[1][1] * (buffer[j * width * 4 + i * 4] & 0xFF) + kernelX[1][2] * (buffer[j * width * 4 + i * 4 + 4] & 0xFF) +
                        kernelX[2][0] * (buffer[(j + 1) * width * 4 + i * 4 - 4] & 0xFF) + kernelX[2][1] * (buffer[(j + 1) * width * 4 + i * 4] & 0xFF) + kernelX[2][2] * (buffer[(j + 1) * width * 4 + i * 4 + 4] & 0xFF);
                int gy = kernelY[0][0] * (buffer[(j - 1) * width * 4 + i * 4 - 4] & 0xFF) + kernelY[0][1] * (buffer[(j - 1) * width * 4 + i * 4] & 0xFF) + kernelY[0][2] * (buffer[(j - 1) * width * 4 + i * 4 + 4] & 0xFF) +
                        kernelY[1][0] * (buffer[j * width * 4 + i * 4 - 4] & 0xFF) + kernelY[1][1] * (buffer[j * width * 4 + i * 4] & 0xFF) + kernelY[1][2] * (buffer[j * width * 4 + i * 4 + 4] & 0xFF) +
                        kernelY[2][0] * (buffer[(j + 1) * width * 4 + i * 4 - 4] & 0xFF) + kernelY[2][1] * (buffer[(j + 1) * width * 4 + i * 4] & 0xFF) + kernelY[2][2] * (buffer[(j + 1) * width * 4 + i * 4 + 4] & 0xFF);

                double g = Math.sqrt(gx * gx + gy * gy);
                if (g > maxG) maxG = g;

                gList[(width - 2) * (j - 1) + (i - 1)] = g;
            }
        }

        for (int j = 1; j < height - 1; j++) {
            for (int i = 1; i < width - 1; i++) {
                byte g = (byte) (0xFF * gList[(width - 2) * (j - 1) + (i - 1)] / maxG);
                buffer2[(j - 1) * (width - 2) * 4 + (i - 1) * 4] = buffer2[(j - 1) * (width - 2) * 4 + (i - 1) * 4 + 1] = buffer2[(j - 1) * (width - 2) * 4 + (i - 1) * 4 + 2] = g;
                buffer2[(j - 1) * (width - 2) * 4 + (i - 1) * 4 + 3] = (byte) 0xFF;
            }
        }

        edgeImage = new WritableImage(width - 2, height - 2) {{
            PixelWriter writer = getPixelWriter();
            writer.setPixels(0, 0, width - 2, height - 2, PixelFormat.getByteBgraInstance(), buffer2, 0, (width - 2) * 4);
        }};
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        originalImageView.setImage(originalImage);
        greyscaleImageView.setImage(greyscaleImage);
        edgeImageView.setImage(edgeImage);
    }
}