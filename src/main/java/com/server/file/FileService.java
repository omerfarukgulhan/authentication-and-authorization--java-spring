package com.server.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import com.server.config.AuthProperties;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    private final AuthProperties authProperties;

    Tika tika = new Tika();

    @Autowired
    public FileService(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    public String saveBase64StringAsFile(String image) {
        String filename = UUID.randomUUID().toString();

        Path path = getProfileImagePath(filename);
        try {
            OutputStream outputStream = new FileOutputStream(path.toFile());
            outputStream.write(decodedImage(image));
            outputStream.close();
            return filename;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String detectType(String value) {
        return tika.detect(decodedImage(value));
    }

    private byte[] decodedImage(String encodedImage) {
        return Base64.getDecoder().decode(encodedImage.split(",")[1]);
    }

    public void deleteProfileImage(String image) {
        if (image == null) return;
        Path path = getProfileImagePath(image);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getProfileImagePath(String filename) {
        return Paths.get(authProperties.getStorage().getRoot(), authProperties.getStorage().getProfile(), filename);
    }
}