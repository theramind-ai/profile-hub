package com.profilehub.util;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
public class FileUtils {

    private static final String UPLOAD_DIR = "uploads/documents";
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    public static String saveFile(byte[] fileContent, String originalFileName) {
        try {
            String uploadPath = UPLOAD_DIR + File.separator + UUID.randomUUID() + "_" + originalFileName;
            Path path = Paths.get(uploadPath);

            // Create directories if they don't exist
            Files.createDirectories(path.getParent());
            Files.write(path, fileContent);

            return uploadPath;
        } catch (Exception e) {
            log.error("Error saving file: {}", e.getMessage());
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public static void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted: {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
        }
    }

    public static boolean isValidFileSize(long fileSize) {
        return fileSize <= MAX_FILE_SIZE;
    }

    public static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf + 1);
    }

    public static boolean isAllowedFileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return extension.matches("(pdf|doc|docx|xlsx|xls|ppt|pptx|jpg|jpeg|png|gif|zip|rar|txt)");
    }

    public static byte[] readFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            }
            throw new RuntimeException("File not found: " + filePath);
        } catch (Exception e) {
            log.error("Error reading file: {}", e.getMessage());
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
