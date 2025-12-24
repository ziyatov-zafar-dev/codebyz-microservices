package uz.codebyz.message.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class FileStorageService {

    @Value("${storage.uploads-dir}")
    private String uploadsDir;

    @Value("${storage.profile.dir}")
    private String baseDir;

    private static final long MAX_SIZE_BYTES = 200L * 1024 * 1024; // 200 MB

    public StoredFile save(String fileType, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("File size exceeds 200MB limit");
        }
        LocalDateTime now = LocalDateTime.now();

        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());
        String hour = String.format("%02d", now.getHour());
        String minute = String.format("%02d", now.getMinute());
        String second = String.format("%02d", now.getSecond());
        String millisecond = String.format("%03d", now.getNano() / 1_000_000);

        Path dir = Path.of(
                uploadsDir,
                baseDir,
                sanitize(fileType),
                year,
                month,
                day,
                hour + "-" + minute + "-" + second,
                millisecond
        );

        Files.createDirectories(dir);

        String originalName = sanitize(file.getOriginalFilename());
        Path target = dir.resolve(originalName);
        Files.copy(file.getInputStream(), target);

        String relativePath = String.join("/",
                baseDir,
                sanitize(fileType),
                year,
                month,
                day,
                hour + "-" + minute + "-" + second,
                millisecond,
                originalName
        );

        return new StoredFile(originalName, relativePath, file.getSize());
    }

    public void delete(String relativePath) {
        try {
            if (relativePath == null || relativePath.isBlank()) return;
            Path path = Path.of(uploadsDir, relativePath);
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }

    private String sanitize(String name) {
        if (name == null) return sanitize(new Date().toString());
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public record StoredFile(String filename, String relativePath, long size) {
    }
}
