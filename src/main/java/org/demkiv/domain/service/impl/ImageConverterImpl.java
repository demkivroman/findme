package org.demkiv.domain.service.impl;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.demkiv.domain.service.ImageConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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

    @Override
    public byte[] cropFaceToSquare(byte[] originalBytes, int size) throws IOException {
        // Load image into Mat
        Mat image = opencv_imgcodecs.imdecode(new Mat(new BytePointer(originalBytes)), opencv_imgcodecs.IMREAD_COLOR);
        if (image.empty()) {
            throw new IOException("Failed to decode image");
        }

        // Load cascade from resource
        File cascadeFile = loadCascadeFromResources("/haarcascade_frontalface_default.xml");
        CascadeClassifier faceDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
        if (faceDetector.empty()) {
            throw new IOException("Failed to load cascade classifier");
        }

        // Detect faces
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(image, faces);
        if (faces.size() == 0) {
            throw new IOException("No faces found");
        }

        // Take first face
        Rect face = faces.get(0);

        // Optional padding
        int padding = (int) (0.2 * Math.max(face.width(), face.height()));
        int x = Math.max(face.x() - padding, 0);
        int y = Math.max(face.y() - padding, 0);
        int width = Math.min(face.width() + 2 * padding, image.cols() - x);
        int height = Math.min(face.height() + 2 * padding, image.rows() - y);
        Rect faceRect = new Rect(x, y, width, height);

        // Crop the face
        Mat faceMat = new Mat(image, faceRect);

        // Resize to target size
        Mat resizedFace = new Mat();
        opencv_imgproc.resize(faceMat, resizedFace, new Size(size, size));

        // Encode to JPEG
        BytePointer buf = new BytePointer();
        opencv_imgcodecs.imencode(".jpg", resizedFace, buf);
        byte[] byteArray = new byte[(int) buf.limit()];
        buf.get(byteArray);

        // Clean up
        buf.deallocate();
        cascadeFile.delete();

        return byteArray;
    }

    private File loadCascadeFromResources(String resourcePath) throws IOException {
        InputStream is = getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }
        File tempFile = File.createTempFile("haarcascade", ".xml");
        try (OutputStream os = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        }
        return tempFile;
    }
}
