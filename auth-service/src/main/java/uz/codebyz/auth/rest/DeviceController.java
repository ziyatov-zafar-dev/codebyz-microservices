package uz.codebyz.auth.rest;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.dto.DeviceResponse;
import uz.codebyz.auth.security.JwtUser;
import uz.codebyz.auth.service.DeviceService;

import java.util.List;

@RestController
@RequestMapping("/api/auth/devices")
public class DeviceController {
    private final DeviceService service;

    public DeviceController(DeviceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseDto<List<DeviceResponse>> myDevices(
            @AuthenticationPrincipal JwtUser user,
            @RequestHeader("X-Device-Id") String deviceId
    ) {
        return service.myDevices(user.getUserId(), deviceId);
    }

    @PostMapping("/logout/{deviceId}")
    public ResponseDto<Void> logoutDevice(@AuthenticationPrincipal JwtUser user,
                                          @PathVariable("deviceId") String deviceId,
                                          HttpServletRequest req) {
        String jti = (String) req.getAttribute("jti");
        return service.logoutDevice(user.getUserId(), deviceId, jti);
    }

    @PostMapping("/logout-all")
    public ResponseDto<Void> logoutAll(@AuthenticationPrincipal JwtUser user, @RequestHeader("X-Device-Id") String deviceId
    ) {
        return service.logoutAll(user.getUserId(), deviceId);
    }
}
