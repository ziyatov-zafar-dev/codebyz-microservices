package uz.codebyz.ads.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.ads.common.ErrorCode;
import uz.codebyz.ads.common.ResponseDto;
import uz.codebyz.ads.controller.FileController.MediaKind;
import uz.codebyz.ads.domain.Ad;
import uz.codebyz.ads.domain.AdAudienceRole;
import uz.codebyz.ads.domain.AdStatus;
import uz.codebyz.ads.domain.AdType;
import uz.codebyz.ads.security.JwtUser;
import uz.codebyz.ads.storage.FileStorageService;
import uz.codebyz.ads.service.AdService;
import uz.codebyz.ads.service.AuthUserClient;

import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/ads")
@Tag(name = "Reklama", description = "Foydalanuvchiga ko'rinadigan reklama boshqaruvi va rolda filtrlash")
public class AdApiController {

    private static final long MAX_MEDIA_BYTES = 200 * 1024 * 1024; // 200MB
    private static final Set<String> IMAGE_TYPES = Set.of("image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif");
    private static final Set<String> VIDEO_TYPES = Set.of("video/mp4", "video/mpeg", "video/quicktime", "video/webm");
    private static final Set<String> AUDIO_TYPES = Set.of("audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg", "audio/webm");

    private final AdService adService;
    private final AuthUserClient authUserClient;
    private final FileStorageService fileStorageService;
    private final String publicUrl;

    public AdApiController(AdService adService,
                           AuthUserClient authUserClient,
                           FileStorageService fileStorageService,
                           @org.springframework.beans.factory.annotation.Value("${storage.ad.public-url:/files}") String publicUrl) {
        this.adService = adService;
        this.authUserClient = authUserClient;
        this.fileStorageService = fileStorageService;
        this.publicUrl = publicUrl;
    }

    // ------------- Public ko'rinish -------------
    @GetMapping("/public")
    @Operation(summary = "Foydalanuvchiga ko'rinadigan reklamalar", description = "ROLE, vaqt va joylashuv bo'yicha filtrlash, priority tartibida (ACTIVE, muddati tugamagan).")
    public ResponseEntity<ResponseDto<List<AdResponse>>> listPublic(@AuthenticationPrincipal JwtUser principal,
                                                                    @RequestParam(name = "page", required = false) String page,
                                                                    @RequestParam(name = "section", required = false) String section,
                                                                    @RequestParam(name = "position", required = false) String position,
                                                                    @RequestParam(name = "type", required = false) AdType type,
                                                                    @RequestParam(name = "limit", defaultValue = "20") @Min(1) @Max(100) int limit) {
        String role = principal != null ? principal.getRole() : null;
        List<AdResponse> ads = adService.listForAudience(role, page, section, position, type, limit).stream()
                .map(AdResponse::from)
                .toList();
        return ResponseEntity.ok(ResponseDto.ok("ok", ads));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Bitta reklamani olish", description = "Faqat ACTIVE va amal muddati tugamagan reklama; roli mos bo'lishi shart.")
    public ResponseEntity<ResponseDto<AdResponse>> getPublic(@PathVariable("id") UUID id,
                                                             @AuthenticationPrincipal JwtUser principal) {
        String role = principal != null ? principal.getRole() : null;
        return adService.find(id)
                .filter(ad -> ad.isActiveNow(Instant.now()) && ad.canShowForRole(role))
                .map(ad -> ResponseEntity.ok(ResponseDto.ok("ok", AdResponse.from(ad))))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDto.<AdResponse>fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi")));
    }

    // ------------- Admin CRUD -------------
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Admin ro'yxati", description = "Pagination + filtrlar (status, page, section, position, type).")
    public ResponseEntity<ResponseDto<PageResponse<AdResponse>>> listAdmin(@AuthenticationPrincipal JwtUser principal,
                                                                           @RequestParam(name = "page", defaultValue = "0") @Min(0) int page,
                                                                           @RequestParam(name = "size", defaultValue = "20") @Min(1) @Max(100) int size,
                                                                           @RequestParam(name = "status", required = false) AdStatus status,
                                                                           @RequestParam(name = "pageKey", required = false) String pageKey,
                                                                           @RequestParam(name = "section", required = false) String section,
                                                                           @RequestParam(name = "position", required = false) String position,
                                                                           @RequestParam(name = "type", required = false) AdType type) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        var result = adService.listAdmin(page, size, status, pageKey, section, position, type);
        List<AdResponse> data = result.items().stream().map(AdResponse::from).toList();
        return ResponseEntity.ok(ResponseDto.ok("ok", new PageResponse<>(result.total(), page, size, data)));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reklama yaratish", responses = {
            @ApiResponse(responseCode = "201", description = "Yaratildi", content = @Content(schema = @Schema(implementation = AdResponse.class)))
    })
    public ResponseEntity<ResponseDto<AdResponse>> create(@Valid @RequestBody CreateAdRequest req,
                                                          @AuthenticationPrincipal JwtUser principal) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        if (!authUserClient.userExists(principal.getUserId())) {
            return ResponseEntity.status(404).body(ResponseDto.<AdResponse>fail(404, ErrorCode.NOT_FOUND, "Foydalanuvchi topilmadi"));
        }
        if (req.targetUrl != null && !req.targetUrl.isBlank() && !isValidUrl(req.targetUrl)) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.BAD_REQUEST, "targetUrl noto'g'ri format"));
        }
        Ad ad = adService.create(
                principal.getUserId(),
                req.title,
                req.description,
                req.mediaUrl,
                req.mediaPath,
                req.targetUrl,
                req.type,
                req.page,
                req.section,
                req.position,
                req.startAt,
                req.endAt,
                req.status == null ? AdStatus.DRAFT : req.status,
                req.priority == null ? 0 : req.priority,
                toRoleSet(req.audienceRoles)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.ok("Yaratildi", AdResponse.from(ad)));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reklama tahrirlash", description = "ADMIN yangilaydi: matn, mediaUrl/mediaPath, targetUrl, placement, vaqtlar, priority, audienceRoles.")
    public ResponseEntity<ResponseDto<AdResponse>> update(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                          @Valid @RequestBody UpdateAdRequest req,
                                                          @AuthenticationPrincipal JwtUser principal) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        if (req.targetUrl != null && !req.targetUrl.isBlank() && !isValidUrl(req.targetUrl)) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.BAD_REQUEST, "targetUrl noto'g'ri format"));
        }
        return adService.update(
                        id,
                        principal.getUserId(),
                        true,
                        req.title,
                        req.description,
                        req.mediaUrl,
                        req.mediaPath,
                        req.targetUrl,
                        req.type,
                        req.page,
                        req.section,
                        req.position,
                        req.startAt,
                        req.endAt,
                        req.priority,
                        toRoleSet(req.audienceRoles)
                )
                .map(ad -> ResponseEntity.ok(ResponseDto.ok("Yangilandi", AdResponse.from(ad))))
                .orElse(ResponseEntity.status(404).body(ResponseDto.<AdResponse>fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi")));
    }

    @PostMapping("/{id}/status")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Statusni o'zgartirish", description = "DRAFT/ACTIVE/INACTIVE, EXPIRED vaqt tugaganda avtomatik.")
    public ResponseEntity<ResponseDto<AdResponse>> changeStatus(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                                @Parameter(description = "Yangi status (DRAFT/ACTIVE/INACTIVE)") @Valid @RequestBody ChangeStatusRequest req,
                                                                @AuthenticationPrincipal JwtUser principal) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        if (req.status == AdStatus.EXPIRED) {
            return ResponseEntity.badRequest().body(ResponseDto.<AdResponse>fail(400, ErrorCode.BAD_REQUEST, "EXPIRED qo'lda berilmaydi"));
        }
        return adService.changeStatus(id, principal.getUserId(), true, req.status)
                .map(ad -> ResponseEntity.ok(ResponseDto.ok("Status yangilandi", AdResponse.from(ad))))
                .orElse(ResponseEntity.status(404).body(ResponseDto.<AdResponse>fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi")));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reklama o'chirish", description = "Soft delete: status INACTIVE ga o'tadi, media o'chirilmaydi.")
    public ResponseEntity<ResponseDto<Object>> delete(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                      @AuthenticationPrincipal JwtUser principal) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        boolean removed = adService.softDelete(id, principal.getUserId(), true);
        if (!removed) {
            return ResponseEntity.status(404).body(ResponseDto.<Object>fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi"));
        }
        return ResponseEntity.ok(ResponseDto.ok("O'chirildi"));
    }

    @PostMapping("/{id}/media")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Reklama media yuklash va biriktirish",
            description = "ADMIN: 200MB limit, MIME tekshiruvi (image/video/audio). mediaPath/mediaUrl yangilanadi, eski fayl o'chiriladi."
    )
    public ResponseEntity<ResponseDto<AdResponse>> uploadMedia(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                               @AuthenticationPrincipal JwtUser principal,
                                                               @Parameter(description = "Yuklanadigan media fayl (200MB limit, image/video/audio)") @RequestPart("file") MultipartFile file,
                                                               @Parameter(description = "Media turi (IMAGE/VIDEO/AUDIO/DOCUMENT/OTHER), bo'lmasa MIME bo'yicha autodetect)") @RequestParam(name = "kind", required = false) MediaKind kind,
                                                               HttpServletRequest req) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        var adOpt = adService.find(id);
        if (adOpt.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseDto.fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi"));
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.FILE_REQUIRED, "File required"));
        }
        if (file.getSize() > MAX_MEDIA_BYTES) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.INVALID_FILE, "Fayl juda katta (200MB limit)"));
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        MediaKind resolvedKind = kind != null ? kind : detectKind(contentType);
        if (!isAllowed(resolvedKind, contentType)) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(400, ErrorCode.INVALID_FILE, "Noto'g'ri fayl turi: " + contentType));
        }
        String oldPath = adOpt.get().getMediaPath();
        try {
            var stored = fileStorageService.saveAdFile(file);
            String url = buildPublicUrl(req, stored.relativePath());
            adService.attachMedia(id, principal.getUserId(), true, stored.relativePath(), url);
            if (oldPath != null && !oldPath.isBlank() && !oldPath.equals(stored.relativePath())) {
                fileStorageService.delete(oldPath);
            }
            return adService.find(id)
                    .map(ad -> ResponseEntity.ok(ResponseDto.ok("Media biriktirildi", AdResponse.from(ad))))
                    .orElse(ResponseEntity.status(404).body(ResponseDto.fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ResponseDto.fail(500, ErrorCode.FILE_SAVE_ERROR, "Upload failed"));
        }
    }

    @DeleteMapping("/{id}/media")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reklama mediani o'chirish", description = "Ad dan mediaPath/mediaUrl tozalanadi va fayl ham o'chiriladi (ADMIN).")
    public ResponseEntity<ResponseDto<Object>> deleteMedia(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                           @AuthenticationPrincipal JwtUser principal) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        var adOpt = adService.find(id);
        if (adOpt.isEmpty()) {
            return ResponseEntity.status(404).body(ResponseDto.fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi"));
        }
        String oldPath = adOpt.get().getMediaPath();
        adService.clearMedia(id, principal.getUserId(), true);
        if (oldPath != null && !oldPath.isBlank()) {
            fileStorageService.delete(oldPath);
        }
        return ResponseEntity.ok(ResponseDto.ok("Media o'chirildi"));
    }

    // ------------- Statistika -------------
    @PostMapping("/{id}/view")
    @Operation(summary = "Ko'rish sanagichini oshirish", description = "Minimal himoya: 30s ichida bir marta")
    public ResponseEntity<ResponseDto<Object>> view(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                    @AuthenticationPrincipal JwtUser principal) {
        UUID userId = principal != null ? principal.getUserId() : null;
        var adOpt = adService.find(id);
        if (adOpt.isEmpty() || !adOpt.get().isActiveNow(Instant.now())) {
            return ResponseEntity.status(404).body(ResponseDto.<Object>fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi yoki faol emas"));
        }
        boolean counted = adService.recordView(id, userId);
        String message = counted ? "Ko'rish hisoblandi" : "Ko'rish hisoblanmadi (throttle)";
        return ResponseEntity.ok(ResponseDto.ok(message));
    }

    @PostMapping("/{id}/click")
    @Operation(summary = "Bosish sanagichini oshirish", description = "Minimal himoya: 15s ichida bir marta")
    public ResponseEntity<ResponseDto<Object>> click(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                     @AuthenticationPrincipal JwtUser principal) {
        UUID userId = principal != null ? principal.getUserId() : null;
        var adOpt = adService.find(id);
        if (adOpt.isEmpty() || !adOpt.get().isActiveNow(Instant.now())) {
            return ResponseEntity.status(404).body(ResponseDto.<Object>fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi yoki faol emas"));
        }
        boolean counted = adService.recordClick(id, userId);
        String message = counted ? "Bosish hisoblandi" : "Bosish hisoblanmadi (throttle)";
        return ResponseEntity.ok(ResponseDto.ok(message));
    }

    @GetMapping("/{id}/stats")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Statistika", description = "Adminlar uchun view/click sonlari")
    public ResponseEntity<ResponseDto<StatsResponse>> stats(@Parameter(description = "Reklama ID (UUID)") @PathVariable("id") UUID id,
                                                           @AuthenticationPrincipal JwtUser principal) {
        if (!isAdmin(principal)) {
            return forbidden();
        }
        return adService.find(id)
                .map(ad -> ResponseEntity.ok(ResponseDto.ok("ok", new StatsResponse(ad.getViewCount(), ad.getClickCount()))))
                .orElse(ResponseEntity.status(404).body(ResponseDto.<StatsResponse>fail(404, ErrorCode.NOT_FOUND, "Reklama topilmadi")));
    }

    private <T> ResponseEntity<ResponseDto<T>> forbidden() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseDto.fail(403, ErrorCode.FORBIDDEN, "Ruxsat berilmagan"));
    }

    private boolean isAdmin(JwtUser principal) {
        return principal != null && "ADMIN".equalsIgnoreCase(principal.getRole());
    }

    private boolean isValidUrl(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String scheme = uri.getScheme();
            return scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"));
        } catch (Exception e) {
            return false;
        }
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

    private Set<AdAudienceRole> toRoleSet(Set<AdAudienceRole> roles) {
        return roles == null || roles.isEmpty() ? EnumSet.of(AdAudienceRole.ALL) : EnumSet.copyOf(roles);
    }

    // ---------- DTO lar ----------
    public record PageResponse<T>(
            @Schema(description = "Umumiy elementlar soni") long total,
            @Schema(description = "Joriy sahifa (0-based)") int page,
            @Schema(description = "Sahifa hajmi") int size,
            @Schema(description = "Natijalar ro'yxati") List<T> items) {
    }

    public record AdResponse(
            @Schema(description = "Reklama ID (UUID)") UUID id,
            @Schema(description = "Yaratuvchi foydalanuvchi ID") UUID ownerId,
            @Schema(description = "Sarlavha (max 140)") String title,
            @Schema(description = "Tavsif (max 2048)") String description,
            @Schema(description = "Media URL (public)") String mediaUrl,
            @Schema(description = "Media serverdagi path (DB)") String mediaPath,
            @Schema(description = "Klik bo'lganda boriladigan URL (http/https)") String targetUrl,
            @Schema(description = "Reklama turi (BANNER/POPUP/VIDEO/TEXT/OTHER)") AdType type,
            @Schema(description = "Sahifa kaliti (masalan, home)") String page,
            @Schema(description = "Bo'lim (masalan, header)") String section,
            @Schema(description = "Pozitsiya/slot nomi") String position,
            @Schema(description = "Status (DRAFT/ACTIVE/INACTIVE/EXPIRED)") AdStatus status,
            @ArraySchema(schema = @Schema(implementation = AdAudienceRole.class, description = "Ko'rish mumkin bo'lgan rollar")) Set<AdAudienceRole> audienceRoles,
            @Schema(description = "Prioritet (katta bo'lsa oldin)") int priority,
            @Schema(description = "Boshlanish vaqti (ixtiyoriy)") Instant startAt,
            @Schema(description = "Tugash vaqti (ixtiyoriy)") Instant endAt,
            @Schema(description = "Yaratilgan vaqti") Instant createdAt,
            @Schema(description = "Yangilangan vaqti") Instant updatedAt,
            @Schema(description = "Kim tomonidan oxirgi yangilangan (UUID)") UUID lastUpdatedBy) {
        public static AdResponse from(Ad ad) {
            return new AdResponse(
                    ad.getId(),
                    ad.getOwnerId(),
                    ad.getTitle(),
                    ad.getDescription(),
                    ad.getMediaUrl(),
                    ad.getMediaPath(),
                    ad.getTargetUrl(),
                    ad.getType(),
                    ad.getPage(),
                    ad.getSection(),
                    ad.getPosition(),
                    ad.getStatus(),
                    ad.getAudienceRoles(),
                    ad.getPriority(),
                    ad.getStartAt(),
                    ad.getEndAt(),
                    ad.getCreatedAt(),
                    ad.getUpdatedAt(),
                    ad.getLastUpdatedBy()
            );
        }
    }

    public record StatsResponse(
            @Schema(description = "Umumiy ko'rishlar soni") long views,
            @Schema(description = "Umumiy bosishlar soni") long clicks) {
    }

    public static class CreateAdRequest {
        @Schema(description = "Sarlavha (majburiy, max 140)")
        @NotBlank
        @Size(max = 140)
        public String title;
        @Schema(description = "Tavsif (majburiy, max 2048)")
        @NotBlank
        @Size(max = 2048)
        public String description;
        @Schema(description = "Media URL (ixtiyoriy, public)")
        @Size(max = 1024)
        public String mediaUrl;
        @Schema(description = "Media server path (ixtiyoriy, upload natijasi)")
        @Size(max = 1024)
        public String mediaPath;
        @Schema(description = "Klik URL (http/https, ixtiyoriy)")
        @Size(max = 1024)
        public String targetUrl;
        @Schema(description = "Reklama turi (majburiy)")
        @NotNull
        public AdType type;
        @Schema(description = "Sahifa kaliti (majburiy)")
        @NotBlank
        public String page;
        @Schema(description = "Bo'lim (majburiy)")
        @NotBlank
        public String section;
        @Schema(description = "Pozitsiya/slot (majburiy)")
        @NotBlank
        public String position;
        @Schema(description = "Boshlanish vaqti (ixtiyoriy)")
        public Instant startAt;
        @Schema(description = "Tugash vaqti (ixtiyoriy)")
        public Instant endAt;
        @Schema(description = "Status (ixtiyoriy, default DRAFT)")
        public AdStatus status;
        @Schema(description = "Prioritet (ixtiyoriy, 0-1000)")
        @Min(0)
        @Max(1000)
        public Integer priority;
        @ArraySchema(schema = @Schema(implementation = AdAudienceRole.class, description = "Ko'rish mumkin bo'lgan rollar; bo'sh bo'lsa ALL"))
        public Set<AdAudienceRole> audienceRoles;
    }

    public static class UpdateAdRequest {
        @Schema(description = "Sarlavha (ixtiyoriy, max 140)")
        @Size(max = 140)
        public String title;
        @Schema(description = "Tavsif (ixtiyoriy, max 2048)")
        @Size(max = 2048)
        public String description;
        @Schema(description = "Media URL (public)")
        @Size(max = 1024)
        public String mediaUrl;
        @Schema(description = "Media server path (upload natijasi)")
        @Size(max = 1024)
        public String mediaPath;
        @Schema(description = "Klik URL (http/https)")
        @Size(max = 1024)
        public String targetUrl;
        @Schema(description = "Reklama turi (ixtiyoriy)")
        public AdType type;
        @Schema(description = "Sahifa kaliti (ixtiyoriy)")
        public String page;
        @Schema(description = "Bo'lim (ixtiyoriy)")
        public String section;
        @Schema(description = "Pozitsiya/slot (ixtiyoriy)")
        public String position;
        @Schema(description = "Boshlanish vaqti (ixtiyoriy)")
        public Instant startAt;
        @Schema(description = "Tugash vaqti (ixtiyoriy)")
        public Instant endAt;
        @Schema(description = "Prioritet (ixtiyoriy, 0-1000)")
        @Min(0)
        @Max(1000)
        public Integer priority;
        @ArraySchema(schema = @Schema(implementation = AdAudienceRole.class, description = "Ko'rish mumkin bo'lgan rollar; bo'sh bo'lsa ALL"))
        public Set<AdAudienceRole> audienceRoles;
    }

    public static class ChangeStatusRequest {
        @Schema(description = "Yangi status (DRAFT/ACTIVE/INACTIVE)")
        @NotNull
        public AdStatus status;
    }
}
