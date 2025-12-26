package uz.codebyz.message.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.codebyz.message.dto.PushSubscriptionRequest;
import uz.codebyz.message.dto.ResponseDto;
import uz.codebyz.message.push.WebPushService;
import uz.codebyz.message.security.JwtPrincipal;
import uz.codebyz.message.security.JwtUser;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/push")
public class PushSubscriptionController {

    private final WebPushService webPushService;

    public PushSubscriptionController(WebPushService webPushService) {
        this.webPushService = webPushService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ResponseDto<Void>> subscribe(@Valid @RequestBody PushSubscriptionRequest request,
                                                       Principal principal) {
        UUID userId = extractUserId(principal);
        webPushService.saveSubscription(userId, request);
        return ResponseEntity.ok(ResponseDto.ok("Subscription saved"));
    }

    private UUID extractUserId(Principal principal) {
        if (principal instanceof JwtPrincipal jwtPrincipal) {
            return jwtPrincipal.getUserId();
        }
        if (principal instanceof JwtUser jwtUser) {
            return jwtUser.getUserId();
        }
        return UUID.fromString(principal.getName());
    }
}
