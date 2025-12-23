package uz.codebyz.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.auth.common.ErrorCode;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.device.UserDevice;
import uz.codebyz.auth.device.UserDeviceRepository;
import uz.codebyz.auth.dto.DeviceResponse;
import uz.codebyz.auth.session.RefreshTokenRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {
    private final UserDeviceRepository deviceRepo;
    private final RefreshTokenRepository refreshRepo;

    public DeviceService(UserDeviceRepository deviceRepo, RefreshTokenRepository refreshRepo) {
        this.deviceRepo = deviceRepo;
        this.refreshRepo = refreshRepo;
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<DeviceResponse>> myDevices(UUID userId, String deviceId) {
        List<UserDevice> devices = deviceRepo.findActiveByUserId(userId);
        List<DeviceResponse> out = new ArrayList<>();
        for (UserDevice d : devices) {
            DeviceResponse r = new DeviceResponse();
            r.setDeviceId(d.getDeviceId());
            r.setActive(d.isActive());
            r.setIp(d.getIp());
            r.setDeviceName(d.getDeviceName());
            r.setCountry(d.getCountry());
            r.setRegion(d.getRegion());
            r.setCity(d.getCity());
            r.setTimezone(d.getTimezone());
            r.setIsp(d.getIsp());
            r.setLastLoginAt(d.getLastLoginAt());
            r.setMe(deviceId.equals(d.getDeviceId()));
            out.add(r);
        }
        return ResponseDto.ok("OK", out);
    }

    public boolean isCurrentDevice(UUID userId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return false;
        }

        return deviceRepo.existsByUserIdAndDeviceId(userId, deviceId);
    }

    @Transactional
    public ResponseDto<Void> logoutDevice(UUID userId, String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return ResponseDto.fail(400, ErrorCode.DEVICE_ID_REQUIRED, "deviceId kerak");
        }
        refreshRepo.revokeByUserAndDevice(userId, deviceId);
        UserDevice d = deviceRepo.findByUserIdAndDeviceId(userId, deviceId).orElse(null);
        if (d != null) {
            d.setActive(false);
            d.setLastLoginAt(Instant.now());
            deviceRepo.save(d);
        }
        return ResponseDto.ok("Logout OK");
    }

    @Transactional
    public ResponseDto<Void> logoutAll(UUID userId, String deviceId) {
        refreshRepo.revokeAllByUser(userId);
        List<UserDevice> devices = deviceRepo.findActiveByUserId(userId);
        for (UserDevice d : devices) {
            if (d.getDeviceId().equals(deviceId)) continue;
            d.setActive(false);
            d.setLastLoginAt(Instant.now());
            d = deviceRepo.save(d);
        }
        return ResponseDto.ok("Logout all OK");
    }

    @Transactional
    public ResponseDto<Void> logoutAll(UUID userId) {
        refreshRepo.revokeAllByUser(userId);
        List<UserDevice> devices = deviceRepo.findActiveByUserId(userId);
        for (UserDevice d : devices) {
            d.setActive(false);
            d.setLastLoginAt(Instant.now());
            try {
                d = deviceRepo.save(d);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return ResponseDto.ok("Logout all OK");
    }
}
