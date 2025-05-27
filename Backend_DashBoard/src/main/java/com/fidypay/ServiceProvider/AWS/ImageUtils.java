package com.fidypay.ServiceProvider.AWS;


import org.imgscalr.Scalr;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public final class ImageUtils {

    public static final int IMAGE_SIZE_500 = 500;
    public static final String IMAGE_SIZE_500_PREFIX = "image500x500/";
    public static final String IMAGE = "image";
    public static final String INVALID_IMAGE_FILE = "Invalid image file";

    private ImageUtils() {
    }

    public static String getUniqueId() {
		return UUID.randomUUID().toString();
	}
    
    public static byte[] resizeImage(MultipartFile file, int width, int height) throws IOException {
        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (file.getContentType() == null || !file.getContentType().toLowerCase().contains(IMAGE)) {
            return new byte[0];
        }
        if (inputImage.getWidth() <= width || inputImage.getHeight() <= height) {
            return file.getBytes();
        }
        ImageIO.write(Scalr.resize(inputImage, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, width, height, Scalr.OP_ANTIALIAS),
                file.getContentType().split("/")[1],
                byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
