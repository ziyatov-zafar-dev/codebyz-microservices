package uz.codebyz.message.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.codebyz.message.security.JwtUser;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/me")
@Tag(name = "Auth Info", description = "Returns current authenticated user info from JWT")
@SecurityRequirement(name = "bearerAuth")
public class AuthInfoController {

    @GetMapping
    @Operation(summary = "Current user", description = "Returns userId and role from the authenticated JWT.")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal JwtUser principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of(
                "userId", principal.getUserId(),
                "role", principal.getRole()
        ));
    }
}
