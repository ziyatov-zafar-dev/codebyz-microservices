package uz.codebyz.auth.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.auth.common.ErrorCode;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.config.properties.JwtProperties;
import uz.codebyz.auth.config.properties.VerificationProperties;
import uz.codebyz.auth.device.DeviceNameUtil;
import uz.codebyz.auth.device.UserDevice;
import uz.codebyz.auth.device.UserDeviceRepository;
import uz.codebyz.auth.device.enums.DeviceType;
import uz.codebyz.auth.dto.*;
import uz.codebyz.auth.guard.LoginBlockedException;
import uz.codebyz.auth.guard.LoginGuardService;
import uz.codebyz.auth.location.IpWhoIsClient;
import uz.codebyz.auth.location.IpWhoIsResponse;
import uz.codebyz.auth.mail.MailService;
import uz.codebyz.auth.security.JwtTokenService;
import uz.codebyz.auth.session.RevokedAccessTokenRepository;
import uz.codebyz.auth.user.User;
import uz.codebyz.auth.user.UserRepository;
import uz.codebyz.auth.user.UserRole;
import uz.codebyz.auth.util.UserAgentUtil;
import uz.codebyz.auth.verification.*;
import uz.codebyz.auth.session.RefreshToken;
import uz.codebyz.auth.session.RefreshTokenRepository;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PendingRegistrationRepository pendingRepo;
    private final EmailVerificationRepository verificationRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final VerificationProperties verificationProps;
    private final JwtTokenService jwtTokenService;
    private final LoginGuardService loginGuardService;
    private final UserDeviceRepository deviceRepo;
    private final IpWhoIsClient ipWhoIsClient;
    private final RefreshTokenRepository refreshRepo;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DeviceService deviceService;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final JwtProperties jwtProperties;

    public AuthService(UserRepository userRepo,
                       PendingRegistrationRepository pendingRepo,
                       EmailVerificationRepository verificationRepo,
                       PasswordEncoder passwordEncoder,
                       MailService mailService,
                       VerificationProperties verificationProps,
                       JwtTokenService jwtTokenService,
                       LoginGuardService loginGuardService,
                       UserDeviceRepository deviceRepo,
                       IpWhoIsClient ipWhoIsClient, RefreshTokenRepository refreshRepo, RefreshTokenRepository refreshTokenRepository, DeviceService deviceService, RevokedAccessTokenRepository revokedAccessTokenRepository, JwtProperties jwtProperties) {
        this.userRepo = userRepo;
        this.pendingRepo = pendingRepo;
        this.verificationRepo = verificationRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.verificationProps = verificationProps;
        this.jwtTokenService = jwtTokenService;
        this.loginGuardService = loginGuardService;
        this.deviceRepo = deviceRepo;
        this.ipWhoIsClient = ipWhoIsClient;
        this.refreshRepo = refreshRepo;
        this.refreshTokenRepository = refreshTokenRepository;
        this.deviceService = deviceService;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public ResponseDto<Void> signUp(SignUpRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        String username = req.getUsername().trim().toLowerCase();

        if (userRepo.existsByEmailIgnoreCase(email) || pendingRepo.existsByEmailIgnoreCase(email)) {
            return ResponseDto.fail(409, ErrorCode.EMAIL_TAKEN, "Email band");
        }
        if (userRepo.existsByUsernameIgnoreCase(username) || pendingRepo.existsByUsernameIgnoreCase(username)) {
            return ResponseDto.fail(409, ErrorCode.USERNAME_TAKEN, "Username band");
        }

        PendingRegistration pr = pendingRepo.findByEmailIgnoreCase(email).orElse(null);
        if (pr == null) pr = new PendingRegistration();
        pr.setFirstname(req.getFirstname().trim());
        pr.setLastname(req.getLastname().trim());
        pr.setUsername(username);
        pr.setEmail(email);
        pr.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        pr.setExpiresAt(Instant.now().plus(verificationProps.getSignUpCodeMinutes(), ChronoUnit.MINUTES));
        pendingRepo.save(pr);

        String code = genCode();
        saveVerification(email, VerificationPurpose.SIGN_UP, code, verificationProps.getSignUpCodeMinutes());
        mailService.sendVerificationCode(email, "CodeByZ Sign Up Code", code, "SIGN_UP");
        return ResponseDto.ok("Kod emailga yuborildi, kod amal qilish muddati %d daqiqa".formatted(verificationProps.getSignInCodeMinutes()));
    }


    @Transactional
    public ResponseDto<AuthTokensResponse> signUpVerify(VerifyCodeRequest req, String deviceId, String ip, String userAgent) {
        if (deviceId == null || deviceId.isBlank()) {
            return ResponseDto.fail(400, ErrorCode.DEVICE_ID_REQUIRED, "X-Device-Id kerak");
        }

        String identifier = req.getIdentifier().trim().toLowerCase();
        PendingRegistration pr = pendingRepo.findByEmailIgnoreCase(identifier).orElse(null);
        if (pr == null) {
            pr = pendingRepo.findByUsernameIgnoreCase(identifier).orElse(null);
        }
        if (pr == null) {
            return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "Pending user topilmadi");
        }
        if (pr.getExpiresAt() != null && pr.getExpiresAt().isBefore(Instant.now())) {
            return ResponseDto.fail(410, ErrorCode.VERIFICATION_EXPIRED, "Registratsiya muddati tugagan, qayta sign-up qiling");
        }

        VerifyResult vr = verify(identifier, VerificationPurpose.SIGN_UP, req.getCode());
        if (!vr.ok) {
            return ResponseDto.fail(vr.httpCode, vr.errorCode, vr.message);
        }

        User u = new User();
        u.setFirstname(pr.getFirstname());
        u.setLastname(pr.getLastname());
        u.setUsername(pr.getUsername());
        u.setEmail(pr.getEmail());
        u.setPasswordHash(pr.getPasswordHash());
        u.setRole(UserRole.STUDENT);
        u.setEmailVerified(true);
        u.setActive(true);

        u = userRepo.save(u);
        pendingRepo.delete(pr);

        ResponseDto<Void> devRes = ensureDeviceAllowed(u.getId(), deviceId, ip, userAgent);
        if (!devRes.isSuccess()) return ResponseDto.fail(devRes.getCode(), devRes.getErrorCode(), devRes.getMessage());

        String jti = UUID.randomUUID().toString();
        String access = jwtTokenService.createAccessToken(u, deviceId, jti);
        String refresh = jwtTokenService.createRefreshToken(u, jti);
        saveRefreshToken(u.getId(), deviceId, jti);
        return ResponseDto.ok("OK", new AuthTokensResponse(access, refresh));
    }

    @Transactional
    public ResponseDto<SignInInitResponse> signIn(SignInRequest req) {
        String identifier = req.getIdentifier().trim().toLowerCase();
        User u = userRepo.findByEmailOrUsername(identifier).orElse(null);
        if (u == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        if (!u.isActive()) return ResponseDto.fail(403, ErrorCode.USER_DISABLED, "User active emas");

        try {
            loginGuardService.ensureNotBlocked(u.getId());
        } catch (LoginBlockedException ex) {
            return ResponseDto.fail(429, ErrorCode.LOGIN_BLOCKED, "Login bloklangan: " + ex.getBlockedUntil());
        }

        boolean ok = passwordEncoder.matches(req.getPassword(), u.getPasswordHash());
        if (!ok) {
            loginGuardService.onFail(u.getId());
            return ResponseDto.fail(401, ErrorCode.BAD_CREDENTIALS, "Login yoki parol xato");
        }
        loginGuardService.onSuccess(u.getId());

        String code = genCode();
        saveVerification(u.getEmail(), VerificationPurpose.SIGN_IN, code, verificationProps.getSignInCodeMinutes());
        mailService.sendVerificationCode(u.getEmail(), "CodeByZ Sign In Code", code, "SIGN_IN");

        return ResponseDto.ok("Kod emailga yuborildi, kod amal qilish muddati %d daqiqa".formatted(verificationProps.getSignInCodeMinutes()), new SignInInitResponse("Kod emailga yuborildi", maskEmail(u.getEmail())));
    }

    private String resolveDeviceId(HttpServletRequest request) {

        // 1️⃣ Frontend yuborgan bo‘lsa
        String deviceId = request.getHeader("X-Device-Id");
        if (deviceId != null && !deviceId.isBlank()) {
            return deviceId.trim();
        }

        // 2️⃣ Cookie’da bo‘lsa
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("DEVICE_ID".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // 3️⃣ Aks holda — yangi deviceId yaratamiz
        return generateDeviceId(request);
    }

    private void setDeviceIdCookie(HttpServletResponse response, String deviceId) {
        Cookie cookie = new Cookie("DEVICE_ID", deviceId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 365); // 1 yil
        response.addCookie(cookie);
    }

    private String generateDeviceId(HttpServletRequest request) {

        String raw = request.getHeader("User-Agent")
                + "|" + clientIp(request)
                + "|" + System.currentTimeMillis();

        return DigestUtils.sha256Hex(raw);
    }

    public static String clientIp(HttpServletRequest request) {

        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "CF-Connecting-IP",      // Cloudflare
                "True-Client-IP",        // Cloudflare (old)
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip)) {
                // Agar bir nechta IP bo‘lsa → birinchisi client
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }


    @Transactional
    public ResponseDto<AuthTokensResponse> signInVerify1(
            VerifyCodeRequest req,
            HttpServletRequest request
    ) {

        // ================= HTTP CONTEXT =================
        String deviceId = resolveDeviceId(request);
        String ip = clientIp(request);
        String userAgent = request.getHeader("User-Agent");

        if (deviceId == null || deviceId.isBlank()) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.DEVICE_ID_REQUIRED,
                    "Device aniqlanmadi"
            );
        }

        // ================= USER =================
        String identifier = req.getIdentifier().trim().toLowerCase();

        User u = userRepo.findByEmailOrUsername(identifier).orElse(null);
        if (u == null) {
            return ResponseDto.fail(
                    404,
                    ErrorCode.USER_NOT_FOUND,
                    "User topilmadi"
            );
        }

        if (!u.isActive()) {
            return ResponseDto.fail(
                    403,
                    ErrorCode.USER_DISABLED,
                    "User active emas"
            );
        }

        if (!u.isEmailVerified()) {
            return ResponseDto.fail(
                    403,
                    ErrorCode.EMAIL_NOT_VERIFIED,
                    "Email verified emas"
            );
        }

        // ================= VERIFY CODE =================
        VerifyResult vr = verify(
                u.getEmail(),
                VerificationPurpose.SIGN_IN,
                req.getCode()
        );

        if (!vr.ok) {
            return ResponseDto.fail(
                    vr.httpCode,
                    vr.errorCode,
                    vr.message
            );
        }

        // ================= DEVICE LIMIT =================
        ResponseDto<Void> devRes =
                ensureDeviceAllowed(u.getId(), deviceId, ip, userAgent);

        if (!devRes.isSuccess()) {
            return ResponseDto.fail(
                    devRes.getCode(),
                    devRes.getErrorCode(),
                    devRes.getMessage()
            );
        }

        // ================= TOKENS =================
        String jti = UUID.randomUUID().toString();

        String access = jwtTokenService.createAccessToken(u, deviceId, jti);
        String refresh = jwtTokenService.createRefreshToken(u, jti);

        saveRefreshToken(u.getId(), deviceId, jti);

        return ResponseDto.ok(
                "Login successful",
                new AuthTokensResponse(access, refresh)
        );
    }


    @Transactional
    public ResponseDto<AuthTokensResponse> signInVerify(VerifyCodeRequest req, String deviceId, String ip, String userAgent) {

        if (deviceId == null || deviceId.isBlank()) {
            return ResponseDto.fail(400, ErrorCode.DEVICE_ID_REQUIRED, "X-Device-Id kerak");
        }

        String identifier = req.getIdentifier().trim().toLowerCase();
        User u = userRepo.findByEmailOrUsername(identifier).orElse(null);
        if (u == null) return ResponseDto.fail(404, ErrorCode.USER_NOT_FOUND, "User topilmadi");
        if (!u.isActive()) return ResponseDto.fail(403, ErrorCode.USER_DISABLED, "User active emas");
        if (!u.isEmailVerified()) return ResponseDto.fail(403, ErrorCode.EMAIL_NOT_VERIFIED, "Email verified emas");

        VerifyResult vr = verify(u.getEmail(), VerificationPurpose.SIGN_IN, req.getCode());
        if (!vr.ok) {
            return ResponseDto.fail(vr.httpCode, vr.errorCode, vr.message);
        }

        ResponseDto<Void> devRes = ensureDeviceAllowed(u.getId(), deviceId, ip, userAgent);
        if (!devRes.isSuccess()) return ResponseDto.fail(devRes.getCode(), devRes.getErrorCode(), devRes.getMessage());

        String jti = UUID.randomUUID().toString();
        String access = jwtTokenService.createAccessToken(u, deviceId, jti);
        String refresh = jwtTokenService.createRefreshToken(u, jti);
        saveRefreshToken(u.getId(), deviceId, jti);
        return ResponseDto.ok("OK", new AuthTokensResponse(access, refresh));
    }

    private void saveRefreshToken(UUID userId, String deviceId, String jti) {
        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setDeviceId(deviceId);
        rt.setJti(jti);
        rt.setRevoked(false);
        rt.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        refreshTokenRepository.save(rt);
    }


    private ResponseDto<Void> ensureDeviceAllowed(UUID userId, String deviceId, String ip, String userAgent) {
        UserDevice existing = deviceRepo.findByUserIdAndDeviceId(userId, deviceId).orElse(null);
        if (existing == null) {
            long activeCount = deviceRepo.countActiveByUserId(userId);
            if (activeCount >= 3) {
                return ResponseDto.fail(403, ErrorCode.DEVICE_LIMIT_EXCEEDED, "Maksimum 3 ta device ruxsat");
            }
            existing = new UserDevice();
            existing.setUserId(userId);
            existing.setDeviceId(deviceId);
        }

        existing.setIp(ip);
        existing.setUserAgent(userAgent);
        existing.setLastLoginAt(Instant.now());
        existing.setActive(true);
        String browserName = UserAgentUtil.getBrowserName(userAgent);
        String browserVersion = UserAgentUtil.getBrowserVersion(userAgent);
        DeviceType deviceType = UserAgentUtil.getDeviceType(userAgent);
        existing.setBrowserName(browserName);
        existing.setBrowserVersion(browserVersion);
        existing.setDeviceType(deviceType);
        existing.setDeviceName(DeviceNameUtil.parse(userAgent));
        deviceRepo.save(existing);
        return ResponseDto.ok("OK");
    }

    @Transactional
    protected void saveVerification(
            String email,
            VerificationPurpose purpose,
            String code,
            long minutes
    ) {
        // 🔥 BARCHA OLDINGI ACTIVE'LARNI O‘CHIRAMIZ
        verificationRepo.deactivateOld(email, purpose);

        EmailVerification ev = new EmailVerification();
        ev.setEmail(email);
        ev.setPurpose(purpose);
        ev.setCode(code);
        ev.setUsed(false);
        ev.setExpiresAt(Instant.now().plus(minutes, ChronoUnit.MINUTES));

        verificationRepo.save(ev);
    }


    private VerifyResult verify(String emailOrIdentifier, VerificationPurpose purpose, String code) {
        String email = emailOrIdentifier;
        EmailVerification ev = verificationRepo.findLatest(email, purpose).orElse(null);
        if (ev == null) return VerifyResult.fail(404, ErrorCode.VERIFICATION_NOT_FOUND, "Kod topilmadi");
        if (ev.isUsed()) return VerifyResult.fail(409, ErrorCode.VERIFICATION_USED, "Kod ishlatilgan");
        if (ev.getExpiresAt() != null && ev.getExpiresAt().isBefore(Instant.now()))
            return VerifyResult.fail(410, ErrorCode.VERIFICATION_EXPIRED, "Kod muddati tugagan");
        if (!ev.getCode().equals(code))
            return VerifyResult.fail(400, ErrorCode.VERIFICATION_INVALID, "Kod noto'g'ri");
        ev.setUsed(true);
        verificationRepo.save(ev);
        return VerifyResult.ok();
    }

    private String genCode() {
        SecureRandom r = new SecureRandom();
        int v = 100000 + r.nextInt(900000);
        return String.valueOf(v);
    }

    private String maskEmail(String email) {
        int at = email.indexOf("@");
        if (at <= 1) return "***" + email.substring(at);
        String name = email.substring(0, at);
        String domain = email.substring(at);
        String masked = name.charAt(0) + "***" + name.charAt(name.length() - 1);
        return masked + domain;
    }


    @Transactional
    public ResponseDto<Void> resetPassword(ResetPasswordRequest request) {

        String identifier = request.getIdentifier().trim().toLowerCase();

        // 1️⃣ USERNI TOPAMIZ
        User user = userRepo.findByEmailOrUsername(identifier)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ VERIFICATION CODE TEKSHIRAMIZ
        VerifyResult vr = verify(
                user.getEmail(),
                VerificationPurpose.FORGOT_PASSWORD,
                request.getCode()
        );

        if (!vr.ok) {
            return ResponseDto.fail(vr.httpCode, vr.errorCode, vr.message);
        }

        // 3️⃣ PASSWORD YANGILAYMIZ
        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword())
        );
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepo.save(user);

        UUID userId = user.getId();

        // ============================
        // 🔥 🔥 🔥 SECURITY CLEANUP
        // ============================

        // 4️⃣ BARCHA REFRESH TOKENLARNI REVOKE QILAMIZ
        refreshTokenRepository.revokeAllByUser(userId);
        revokedAccessTokenRepository.revokeAllByUser(userId);

        // 5️⃣ BARCHA DEVICE’LARNI INACTIVE QILAMIZ
        ResponseDto<Void> success = deviceService.logoutAll(userId);

        deviceRepo.deactivateAllByUser(userId);
        // 6️⃣ LOGIN GUARD RESET
        loginGuardService.onSuccess(userId);
        // yoki agar service bo‘lmasa:
        // loginGuardRepo.deleteByUserId(userId);
        return ResponseDto.ok("Password reset successfully. All sessions logged out.");
    }


    public ResponseDto<Void> forgotPassword(ForgotPasswordRequest request) {

        String identifier = request.getIdentifier().trim().toLowerCase();

        // 1️⃣ USERNI TOPAMIZ (email yoki username)
        User user = userRepo.findByEmailOrUsername(identifier)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String email = user.getEmail();

        // 2️⃣ OLD FORGOT_PASSWORD VERIFICATION BOR BO‘LSA — USED QILAMIZ
        verificationRepo
                .findLatest(email, VerificationPurpose.FORGOT_PASSWORD)
                .ifPresent(ev -> {
                    if (!ev.isUsed()) {
                        ev.setUsed(true);
                        verificationRepo.save(ev);
                    }
                });

        // 3️⃣ 6 XONALI OTP CODE
        String code = String.format(
                "%06d",
                new SecureRandom().nextInt(1_000_000)
        );

        // 4️⃣ EXPIRE TIME — application.yml DAN
        long minutes = verificationProps
                .getPassword()
                .getForgotPasswordCodeMinutes();

        // 5️⃣ EMAIL VERIFICATION ENTITY
        EmailVerification ev = new EmailVerification();
        ev.setEmail(email);
        ev.setPurpose(VerificationPurpose.FORGOT_PASSWORD);
        ev.setCode(code);
        ev.setUsed(false);
        ev.setExpiresAt(
                Instant.now().plus(minutes, ChronoUnit.MINUTES)
        );
        verificationRepo.save(ev);

        // 6️⃣ SENDGRID ORQALI EMAILGA YUBORAMIZ
        mailService.sendVerificationCode(
                email,
                "Password reset verification code",
                code,
                "FORGOT_PASSWORD"
        );

        return ResponseDto.ok("Password reset code sent to email");
    }

    @Transactional
    public ResponseDto<AuthTokensResponse> refreshToken(
            String authHeader,
            String deviceId
    ) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseDto.fail(401, ErrorCode.INVALID_TOKEN, "Refresh token missing");
        }

        String refreshToken = authHeader.substring(7);

        // 1️⃣ REFRESH TOKENNI PARSE QILAMIZ
        Claims claims;
        try {
            claims = jwtTokenService.parseClaims(refreshToken);
        } catch (Exception e) {
            return ResponseDto.fail(401, ErrorCode.INVALID_TOKEN, "Invalid refresh token");
        }

        // 2️⃣ TYPE TEKSHIRAMIZ
        if (!"refresh".equals(claims.get("type", String.class))) {
            return ResponseDto.fail(401, ErrorCode.INVALID_TOKEN, "Not a refresh token");
        }

        UUID userId = UUID.fromString(claims.getSubject());
        String jti = claims.getId();

        // 3️⃣ DB'DAN REFRESH TOKENNI TOPAMIZ
        RefreshToken rt = refreshTokenRepository
                .findByJtiAndDeviceIdAndRevokedFalse(jti, deviceId)
                .orElse(null);

        if (rt == null) {
            return ResponseDto.fail(401, ErrorCode.INVALID_TOKEN, "Refresh token revoked");
        }

        if (rt.getExpiresAt().isBefore(Instant.now())) {
            return ResponseDto.fail(401, ErrorCode.TOKEN_EXPIRED, "Refresh token expired");
        }

        // 4️⃣ USERNI TOPAMIZ
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5️⃣ YANGI ACCESS TOKEN YARATAMIZ (YANGI JTI BILAN)
        String newJti = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenService.createAccessToken(user, deviceId, newJti);

        // 6️⃣ REFRESH TOKEN ROTATION (TAVSIYA ETILADI)
        rt.setRevoked(true);
        refreshTokenRepository.save(rt);

        String newRefreshToken = jwtTokenService.createRefreshToken(user, newJti);

        RefreshToken newRt = new RefreshToken();
        newRt.setUserId(user.getId());
        newRt.setDeviceId(deviceId);
        newRt.setJti(newJti);
        newRt.setRevoked(false);
        newRt.setExpiresAt(
                Instant.now().plus(jwtProperties.getRefreshTokenDays(), ChronoUnit.DAYS)
        );
        refreshTokenRepository.save(newRt);

        // 7️⃣ RESPONSE
        return ResponseDto.ok(
                "Token refreshed",
                new AuthTokensResponse(newAccessToken, newRefreshToken)
        );
    }


    private static class VerifyResult {
        boolean ok;
        int httpCode;
        ErrorCode errorCode;
        String message;

        static VerifyResult ok() {
            VerifyResult r = new VerifyResult();
            r.ok = true;
            return r;
        }

        static VerifyResult fail(int httpCode, ErrorCode ec, String msg) {
            VerifyResult r = new VerifyResult();
            r.ok = false;
            r.httpCode = httpCode;
            r.errorCode = ec;
            r.message = msg;
            return r;
        }
    }
}