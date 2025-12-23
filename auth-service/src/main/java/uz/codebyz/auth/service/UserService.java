package uz.codebyz.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.auth.common.ErrorCode;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.device.UserDeviceRepository;
import uz.codebyz.auth.dto.*;
import uz.codebyz.auth.guard.LoginGuardService;
import uz.codebyz.auth.security.JwtTokenService;
import uz.codebyz.auth.session.RefreshTokenRepository;
import uz.codebyz.auth.session.RevokedAccessTokenRepository;
import uz.codebyz.auth.storage.FileStorageService;
import uz.codebyz.auth.user.User;
import uz.codebyz.auth.user.UserRepository;
import uz.codebyz.auth.util.FileUtil;

import java.time.*;
import java.util.UUID;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final LoginGuardService loginGuardService;
    private final DeviceService deviceService;
    private final JwtTokenService jwtTokenService;
    @Value("${app.timezone}")
    private String timezone;
    private final UserRepository userRepository;
    private final UserDeviceRepository userDeviceRepository;
    @Value("${storage.profile.public-url}")
    private String publicUrl;
    private final UserRepository repo;
    private final FileStorageService storageService;

    public UserService(UserRepository repo, FileStorageService storageService, UserRepository userRepository, UserDeviceRepository userDeviceRepository, PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository, RevokedAccessTokenRepository revokedAccessTokenRepository, LoginGuardService loginGuardService, DeviceService deviceService, JwtTokenService jwtTokenService) {
        this.repo = repo;
        this.storageService = storageService;
        this.userRepository = userRepository;
        this.userDeviceRepository = userDeviceRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.loginGuardService = loginGuardService;
        this.deviceService = deviceService;
        this.jwtTokenService = jwtTokenService;
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
    public ResponseDto<Void> uploadProfileImage(UUID userId, MultipartFile file, HttpServletRequest req) {

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
            user.setAvatarUrl(getHttpUrl(req) + publicUrl + "/" + stored.relativePath());
            user.setUploadedImageTime(Instant.now());
            userRepository.save(user);
            return ResponseDto.ok("Profile image uploaded");

        } catch (Exception e) {
            return ResponseDto.fail(500, ErrorCode.FILE_SAVE_ERROR, "Upload failed");
        }
    }

    private String getHttpUrl(HttpServletRequest req) {
        String scheme = req.getScheme();      // http or https
        String serverName = req.getServerName(); // domain yoki localhost
        int serverPort = req.getServerPort();

        boolean isDefaultPort =
                (scheme.equals("http") && serverPort == 80) ||
                        (scheme.equals("https") && serverPort == 443);

        if (isDefaultPort) {
            return scheme + "://" + serverName;
        }

        return scheme + "://" + serverName + ":" + serverPort;
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

    @Transactional
    public ResponseDto<AuthTokensResponse> changePassword(
            UUID userId,
            String currentDeviceId,
            ChangePasswordRequest request
    ) {

        // 1️⃣ USERNI TOPAMIZ
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ ESKI PAROLNI TEKSHIRAMIZ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.INVALID_PASSWORD,
                    "Old password incorrect"
            );
        }

        // 3️⃣ YANGI PAROL VALIDATSIYASI
        String newPassword = request.getNewPassword();

        if (newPassword == null || newPassword.isBlank()) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.NEW_PASSWORD_REQUIRED,
                    "New password required"
            );
        }

        if (newPassword.length() < 8) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.PASSWORD_WEAK,
                    "Password must be at least 8 characters"
            );
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.SAME_PASSWORD,
                    "New password must be different"
            );
        }

        // 4️⃣ PASSWORDNI YANGILAYMIZ
        user.setPasswordHash(passwordEncoder.encode(newPassword));

        // 🔐 TOKEN VERSION OSHIRAMIZ (ESKI TOKENLAR O‘LADI)
        user.setTokenVersion(user.getTokenVersion() + 1);

        userRepository.save(user);

        UUID uid = user.getId();

        // ============================
        // 🔥 SECURITY CLEANUP
        // ============================

        // 5️⃣ BOSHQA DEVICE’LARNING TOKENLARINI O‘CHIRAMIZ
        refreshTokenRepository.revokeAllExceptDevice(uid, currentDeviceId);
        revokedAccessTokenRepository.revokeAllExceptDevice(uid, currentDeviceId);

        // 6️⃣ BOSHQA DEVICE’LARNI LOGOUT QILAMIZ
        deviceService.logoutAll(uid, currentDeviceId);

        // 7️⃣ LOGIN GUARD RESET
        loginGuardService.onSuccess(uid);
        AuthTokensResponse tokens = jwtTokenService.generateTokens(
                user,
                currentDeviceId
        );
        return ResponseDto.ok(
                "Password changed. Other sessions logged out.",
                tokens
        );

    }


}
