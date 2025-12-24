package uz.codebyz.ads.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.codebyz.ads.common.ResponseDto;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<ResponseDto<Map<String, Object>>> status() {
        return ResponseEntity.ok(ResponseDto.ok("ok", Map.of(
                "service", "ads-service",
                "status", "ok",
                "timestamp", Instant.now().toString()
        )));
    }
}
