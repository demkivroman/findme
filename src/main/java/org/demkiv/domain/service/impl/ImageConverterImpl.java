package org.demkiv.domain.service.impl;

import org.demkiv.domain.service.ImageConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageConverterImpl implements ImageConverter {

    @Override
    public byte[] resizeKeepAspect(byte[] originalBytes, int targetWidth, int targetHeight) throws IOException {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        double widthRatio = (double) targetWidth / originalWidth;
        double heightRatio = (double) targetHeight / originalHeight;
        double scale = Math.min(widthRatio, heightRatio);  // keeps aspect ratio

        int newWidth = (int) (originalWidth * scale);
        int newHeight = (int) (originalHeight * scale);

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", out);
        return out.toByteArray();
    }

    @Override
    public byte[] cropToSquare(byte[] originalBytes, int size) throws IOException {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));
        int width = original.getWidth();
        int height = original.getHeight();

        int squareSize = Math.min(width, height);
        int x = (width - squareSize) / 2;
        int y = (height - squareSize) / 2;

        BufferedImage cropped = original.getSubimage(x, y, squareSize, squareSize);

        BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(cropped, 0, 0, size, size, null);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", out);
        return out.toByteArray();
    }

    @Override
    public byte[] resizeWithPadding(byte[] originalBytes, int size) throws IOException {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalBytes));
        int width = original.getWidth();
        int height = original.getHeight();

        double scale = Math.min((double) size / width, (double) size / height);
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        BufferedImage resized = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();

        // Fill background with white (or any color)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, size, size);

        // Center the image
        int x = (size - newWidth) / 2;
        int y = (size - newHeight) / 2;
        g.drawImage(original, x, y, newWidth, newHeight, null);
        g.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(resized, "jpg", out);
        return out.toByteArray();
    }
}
