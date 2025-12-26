package uz.codebyz.ads.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.ads.common.ErrorCode;
import uz.codebyz.ads.common.ResponseDto;
import uz.codebyz.ads.security.JwtUser;
import uz.codebyz.ads.storage.FileStorageService;
import uz.codebyz.ads.util.FileUtil;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/ads/files")
@Tag(name = "Fayl yuklash", description = "Reklama fayllarini serverga saqlash (ADMIN)")
public class FileController {

    private static final long MAX_FILE_SIZE_BYTES = 200 * 1024 * 1024; // 200 MB
    private static final Set<String> IMAGE_TYPES = Set.of("image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4","video/mkv", "video/mpeg", "video/quicktime", "video/webm");
    private static final Set<String> AUDIO_TYPES = Set.of("audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg", "audio/webm");

    private final FileStorageService storageService;
    private final String publicUrl;
    private final Clock clock;

    public FileController(FileStorageService storageService,
                          @org.springframework.beans.factory.annotation.Value("${storage.ad.public-url:/files}") String publicUrl,
                          Clock clock) {
        this.storageService = storageService;
        this.publicUrl = publicUrl;
        this.clock = clock;
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Fayl yuklash", description = "ADMIN: 200MB limit, MIME tekshiruvi (image/video/audio/document/other).")
    public ResponseEntity<ResponseDto<UploadResponse>> upload(@AuthenticationPrincipal JwtUser principal,
                                                              @Parameter(description = "Yuklanadigan fayl (multipart/form-data)") @RequestPart("file") MultipartFile file,
                                                              @Parameter(description = "Media turi (IMAGE/VIDEO/AUDIO/DOCUMENT/OTHER), bo'lmasa MIME bo'yicha autodetect") @RequestParam(name = "kind", required = false) MediaKind kind,
                                                              HttpServletRequest req) {
        if (principal == null || !"ADMIN".equalsIgnoreCase(principal.getRole())) {
            return ResponseEntity.status(403).body(ResponseDto.fail(403, ErrorCode.FORBIDDEN, "Ruxsat berilmagan"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.FILE_REQUIRED, "File required"));
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.INVALID_FILE, "Fayl juda katta (200MB limit)"));
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        MediaKind resolvedKind = kind != null ? kind : detectKind(contentType);
        if (!isAllowed(resolvedKind, contentType)) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.INVALID_FILE, "Noto'g'ri fayl turi: " + contentType));
        }
        try {
            FileStorageService.StoredFile stored = storageService.saveAdFile(file);
            String absoluteUrl = buildPublicUrl(req, stored.relativePath());
            UploadResponse data = new UploadResponse(
                    stored.filename(),
                    stored.relativePath(),
                    absoluteUrl,
                    resolvedKind,
                    contentType,
                    file.getSize(),
                    FileUtil.toMB(file.getSize()),
                    LocalDateTime.now(clock)
            );
            return ResponseEntity.ok(ResponseDto.ok("Yuklandi", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseDto.fail(500, ErrorCode.FILE_SAVE_ERROR, "Upload failed"));
        }
    }

    private String getHttpUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        boolean isDefaultPort = ("http".equals(scheme) && serverPort == 80)
                || ("https".equals(scheme) && serverPort == 443);
        if (isDefaultPort) {
            return scheme + "://" + serverName;
        }
        return scheme + "://" + serverName + ":" + serverPort;
    }

    private String buildPublicUrl(HttpServletRequest req, String relativePath) {
        String prefix = normalizePublicUrl(publicUrl);
        return getHttpUrl(req) + prefix + "/" + relativePath;
    }

    private String normalizePublicUrl(String url) {
        if (url == null || url.isBlank()) return "";
        String result = url.trim();
        if (!result.startsWith("/")) {
            result = "/" + result;
        }
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private boolean isAllowed(MediaKind kind, String contentType) {
        String ct = contentType == null ? "" : contentType.toLowerCase();
        boolean octet = ct.isBlank() || "application/octet-stream".equals(ct);
        return switch (kind) {
            case IMAGE -> octet || ct.startsWith("image/");
            case VIDEO -> octet || ct.startsWith("video/");
            case AUDIO -> octet || ct.startsWith("audio/");
            case DOCUMENT, OTHER -> true;
        };
    }

    private MediaKind detectKind(String contentType) {
        if (contentType == null) return MediaKind.OTHER;
        String ct = contentType.toLowerCase();
        if (ct.startsWith("image/") || IMAGE_TYPES.contains(ct)) return MediaKind.IMAGE;
        if (ct.startsWith("video/") || VIDEO_TYPES.contains(ct)) return MediaKind.VIDEO;
        if (ct.startsWith("audio/") || AUDIO_TYPES.contains(ct)) return MediaKind.AUDIO;
        return MediaKind.OTHER;
    }

    public enum MediaKind {
        IMAGE, VIDEO, AUDIO, DOCUMENT, OTHER
    }

    public record UploadResponse(String filename,
                                 String relativePath,
                                 String url,
                                 MediaKind kind,
                                 String mimeType,
                                 long size,
                                 String sizeHuman,
                                 LocalDateTime uploadedAt) {
    }
}
