package uz.codebyz.auth.rest;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.dto.*;
import uz.codebyz.auth.security.JwtUser;
import uz.codebyz.auth.service.UserService;
import uz.codebyz.auth.user.UserRepository;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final UserRepository userRepository;

    public UserController(UserService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseDto<MeResponse> me(@AuthenticationPrincipal JwtUser user) {
        return service.me(user.getUserId());
    }

    @PostMapping("/change-username")
    public ResponseDto<Void> changeUsername(@AuthenticationPrincipal JwtUser user,
                                            @Valid @RequestBody ChangeUsernameRequest req) {
        return service.changeUsername(user.getUserId(), req);
    }

    @PostMapping("/change-email")
    public ResponseDto<Void> changeEmail(@AuthenticationPrincipal JwtUser user, @Valid @RequestBody ChangeEmailRequest req) {
        return service.changeEmail(user.getUserId(), req);
    }

    @PostMapping("/heartbeat")
    public void heartbeat(@AuthenticationPrincipal JwtUser user) {
        service.heartbeat(user.getUserId());
    }

    @PostMapping("/update-profile")
    public ResponseDto<Void> updateProfile(@AuthenticationPrincipal JwtUser user, @Valid @RequestBody UpdateProfileRequest req) {
        return service.updateProfile(user.getUserId(), req);
    }

    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<Void> uploadProfileImage(
            @AuthenticationPrincipal JwtUser jwtUser,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest req
    ) {
        return service.uploadProfileImage(jwtUser.getUserId(), file,req);
    }

    @Hidden
    @GetMapping(value = "/exists/{userid}")
    public Boolean existsUserById(@PathVariable UUID userid) {
        return userRepository.findById(userid).isPresent();
    }

    @Hidden
    @GetMapping(value = "/exists/user/{userid}")
    public MeResponse findByUserId(@PathVariable UUID userid) {
        return service.me(userid).getData();
    }

    @PostMapping("change-password")
    public ResponseDto<AuthTokensResponse> changePassword(
            @AuthenticationPrincipal JwtUser user,
            @RequestHeader("X-Device-Id") String currentDeviceId,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        return service.changePassword(user.getUserId(), currentDeviceId, req);
    }

    @PostMapping("has-profile-image")
    public ResponseEntity<Boolean> hasProfileImage(
            @AuthenticationPrincipal JwtUser user
    ) {
        return ResponseEntity.ok(service.me(user.getUserId()).getData().getAvatarUrl() != null);
    }
}


