package uz.codebyz.auth.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.dto.*;
import uz.codebyz.auth.security.JwtUser;
import uz.codebyz.auth.service.AuthService;
import uz.codebyz.auth.service.DeviceService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final DeviceService deviceService;

    public AuthController(AuthService authService, DeviceService deviceService) {
        this.authService = authService;
        this.deviceService = deviceService;
    }

    @PostMapping("/sign-up")
    public ResponseDto<Void> signUp(@Valid @RequestBody SignUpRequest req) {
        return authService.signUp(req);
    }

    @PostMapping("/sign-up/verify")
    public ResponseDto<AuthTokensResponse> signUpVerify(
            @Valid @RequestBody VerifyCodeRequest req,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            HttpServletRequest http
    ) {
//        return authService.signUpVerify1(req, http);
        return authService.signUpVerify(req, deviceId, clientIp(http), http.getHeader("User-Agent"));
    }

    @PostMapping("/sign-in")
    public ResponseDto<SignInInitResponse> signIn(@Valid @RequestBody SignInRequest req) {
        return authService.signIn(req);
    }

    @PostMapping("/sign-in/verify")
    public ResponseDto<?> signInVerify(
            @Valid @RequestBody VerifyCodeRequest req,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            HttpServletRequest http
    ) {
        return authService.signInVerify(req, deviceId, clientIp(http), http.getHeader("User-Agent"));
    }

    @PostMapping("/logout")
    public ResponseDto<Void> logout(@AuthenticationPrincipal JwtUser user,
                                    @RequestHeader(value = "X-Device-Id", required = false) String deviceId) {
        return deviceService.logoutDevice(user.getUserId(), deviceId);
    }

    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    @PostMapping("/forgot-password")
    public ResponseDto<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public ResponseDto<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        return authService.resetPassword(request);
    }
}
