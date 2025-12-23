package uz.codebyz.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.auth.common.ErrorCode;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.device.UserDeviceRepository;
import uz.codebyz.auth.dto.*;
import uz.codebyz.auth.storage.FileStorageService;
import uz.codebyz.auth.user.User;
import uz.codebyz.auth.user.UserRepository;
import uz.codebyz.auth.util.FileUtil;

import java.time.*;
import java.util.UUID;

@Service
public class UserService {
    @Value("${app.timezone}")
    private String timezone;
    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;
    @Value("${storage.profile.public-url}")
    private String publicUrl;
    private final UserRepository repo;
    private final FileStorageService storageService;

    public UserService(UserRepository repo, FileStorageService storageService, UserRepository userRepository, UserDeviceRepository userDeviceRepository) {
        this.repo = repo;
        this.storageService = storageService;
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    @Transactional(readOnly = true)
    public ResponseDto<MeResponse> me(UUID userId) {
        User u = repo.findById(userId).orElse(null);
        if (u == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        MeResponse r = new MeResponse();
        r.setId(u.getId());
        r.setFirstname(u.getFirstname());
        r.setLastname(u.getLastname());
        r.setUsername(u.getUsername());
        r.setEmail(u.getEmail());
        r.setRole(u.getRole());
        r.setEmailVerified(u.isEmailVerified());
        r.setActive(u.isActive());
        r.setAvatarUrl(u.getAvatarUrl());
        r.setAvatarName(u.getAvatarName());
        r.setAvatarFilePath(u.getAvatarFilePath());
        r.setUploadedImgTime(u.getUploadedImageTime());
        r.setAvatarSize(u.getAvatarSize());
        r.setAvatarSizeMB(u.getAvatarSizeMB());
        r.setBirthDate(u.getBirthDate());
        r.setLastOnline(u.getLastActivityAt());
        ZonedDateTime lastActivity = u.getLastActivityAt();

        r.setOnline(
                lastActivity != null &&
                        lastActivity.isAfter(
                                ZonedDateTime.now(ZoneId.of(timezone)).minusSeconds(125)
                        )
        );

        r.setSocialLinks(u.getSocialLinks());
        return ResponseDto.ok("OK", r);
    }

    @Transactional
    public ResponseDto<Void> changeUsername(UUID userId, ChangeUsernameRequest req) {
        User u = repo.findById(userId).orElse(null);
        if (u == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        u.setUsername(req.getNewUsername().trim().toLowerCase());
        repo.save(u);
        return ResponseDto.ok("OK");
    }

    @Transactional
    public ResponseDto<Void> changeEmail(UUID userId, ChangeEmailRequest req) {
        User u = repo.findById(userId).orElse(null);
        if (u == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        u.setEmail(req.getNewEmail().trim().toLowerCase());
        u.setEmailVerified(false);
        repo.save(u);
        return ResponseDto.ok("OK");
    }

    @Transactional
    public ResponseDto<Void> updateProfile(UUID userId, UpdateProfileRequest req) {
        User u = repo.findById(userId).orElse(null);
        if (u == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");

        if (req.getFirstname() != null && !req.getFirstname().isBlank()) u.setFirstname(req.getFirstname().trim());
        if (req.getLastname() != null && !req.getLastname().isBlank()) u.setLastname(req.getLastname().trim());
        if (req.getBirthDate() != null) u.setBirthDate(req.getBirthDate());
        if (req.getSocialLinks() != null) u.setSocialLinks(req.getSocialLinks());
        repo.save(u);
        return ResponseDto.ok("OK");
    }


    @Transactional
    public ResponseDto<Void> uploadProfileImage(UUID userId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseDto.fail(400, ErrorCode.FILE_REQUIRED, "File required");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return ResponseDto.fail(400, ErrorCode.INVALID_FILE, "Only image allowed");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // ❗ endi avatarName emas, relativePath bilan o‘chiramiz
        storageService.deleteProfileImage(user.getAvatarFilePath());
        try {
            FileStorageService.StoredFile stored =
                    storageService.saveProfileImage(file);
            user.setAvatarFilePath(stored.relativePath()); // 🔥 MUHIM
            user.setAvatarName(file.getOriginalFilename()); // 🔥 MUHIM
            user.setAvatarSize(file.getSize());
            user.setAvatarSizeMB(FileUtil.toMB(file.getSize()));
            user.setAvatarUrl(publicUrl + "/" + stored.relativePath());
            user.setUploadedImageTime(Instant.now());
            userRepository.save(user);
            return ResponseDto.ok("Profile image uploaded");

        } catch (Exception e) {
            return ResponseDto.fail(500, ErrorCode.FILE_SAVE_ERROR, "Upload failed");
        }
    }

    @Transactional
    public void heartbeat(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastActivityAt(ZonedDateTime
                .now(ZoneId.of(timezone))
        );
        userRepository.save(user);
        System.out.println(user.getAvatarFilePath());
    }

    public boolean existsById(UUID userid) {
        return userRepository.existsById(userid);
    }
}
