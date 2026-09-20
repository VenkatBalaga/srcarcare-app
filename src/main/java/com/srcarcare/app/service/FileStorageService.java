package com.srcarcare.app.service;

import com.srcarcare.app.util.InvalidUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final long MAX_FILE_SIZE = 8L * 1024 * 1024;

    @Value("${srcarcare.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file, String subfolder) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidUploadException("File is too large. Maximum allowed size is 8MB.");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) {
            extension = originalName.substring(dot + 1).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidUploadException("Only image files (jpg, jpeg, png, webp, gif) are allowed.");
        }

        try {
            Path targetDir = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String storedFilename = UUID.randomUUID() + "." + extension;
            Path targetPath = targetDir.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subfolder + "/" + storedFilename;
        } catch (IOException e) {
            throw new InvalidUploadException("Could not store uploaded file: " + e.getMessage());
        }
    }

    public void delete(String webPath) {
        if (webPath == null || !webPath.startsWith("/uploads/")) {
            return;
        }
        try {
            Path path = Paths.get(uploadDir, webPath.substring("/uploads/".length())).toAbsolutePath().normalize();
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // best-effort cleanup
        }
    }
}
