package uz.codebyz.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.codebyz.auth.common.ErrorCode;
import uz.codebyz.auth.common.ResponseDto;
import uz.codebyz.auth.device.UserDevice;
import uz.codebyz.auth.device.UserDeviceRepository;
import uz.codebyz.auth.dto.DeviceResponse;
import uz.codebyz.auth.location.AddressResponse;
import uz.codebyz.auth.location.IpWhoIsClient;
import uz.codebyz.auth.location.IpWhoIsResponse;
import uz.codebyz.auth.session.RefreshTokenRepository;
import uz.codebyz.auth.session.RevokedAccessToken;
import uz.codebyz.auth.session.RevokedAccessTokenRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceService {
    private final UserDeviceRepository deviceRepo;
    private final RefreshTokenRepository refreshRepo;
    private final RevokedAccessTokenRepository revokedAccessTokenRepository;
    private final IpWhoIsClient ipWhoIsClient;

    public DeviceService(UserDeviceRepository deviceRepo, RefreshTokenRepository refreshRepo, RevokedAccessTokenRepository revokedAccessTokenRepository, IpWhoIsClient ipWhoIsClient) {
        this.deviceRepo = deviceRepo;
        this.refreshRepo = refreshRepo;
        this.revokedAccessTokenRepository = revokedAccessTokenRepository;
        this.ipWhoIsClient = ipWhoIsClient;
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<DeviceResponse>> myDevices(UUID userId, String deviceId) {
        List<UserDevice> devices = deviceRepo.findActiveByUserId(userId);
        List<DeviceResponse> out = new ArrayList<>();
        for (UserDevice d : devices) {
            IpWhoIsResponse lookup = ipWhoIsClient.lookup(d.getIp());
            AddressResponse deviceAddress = ipWhoIsClient.getAddress(
                    lookup.getLatitude(),
                    lookup.getLongitude(),
                    lookup.getCountry_code()
            );
            DeviceResponse r = new DeviceResponse(
                    d.getDeviceId(),
                    d.isActive(),
                    d.getIp(),
                    lookup,
                    d.getLastLoginAt(),
                    d.getDeviceName(),
                    deviceId.equals(d.getDeviceId()),
                    d.getBrowserName(),
                    d.getUserAgent(),
                    d.getDeviceType(),
                    d.getBrowserVersion(),
                    deviceAddress,
                    "%s, %s, %s, %s %s, %s"
                            .formatted(
                                    deviceAddress.getResults().get(0).getStreet(),      // Köroğlu Deresi
                                    deviceAddress.getResults().get(0).getSuburb(),      // Cebeci Mahallesi
                                    deviceAddress.getResults().get(0).getCity(),        // Sultangazi
                                    deviceAddress.getResults().get(0).getCounty(),      // İstanbul
                                    deviceAddress.getResults().get(0).getPostcode(),    // 34270
                                    deviceAddress.getResults().get(0).getCountry()      // Türkiye
                            ));
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
    public ResponseDto<Void> logoutDevice(
            UUID userId,
            String deviceId,
            String accessTokenJti
    ) {

        if (deviceId == null || deviceId.isBlank()) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.DEVICE_ID_REQUIRED,
                    "X-Device-Id required"
            );
        }

        // 1️⃣ Refresh tokenni revoke qilamiz
        refreshRepo.revokeByUserAndDevice(userId, deviceId);

        // 2️⃣ Access tokenni revoke qilamiz (JTI bilan)
        RevokedAccessToken rat = new RevokedAccessToken();
        rat.setJti(accessTokenJti);
        rat.setUserId(userId);
        rat.setDeviceId(deviceId);
        rat.setRevokedAt(Instant.now());
        revokedAccessTokenRepository.save(rat);

        // 3️⃣ Device’ni deactivate qilamiz
        deviceRepo.findByUserIdAndDeviceId(userId, deviceId)
                .ifPresent(d -> {
                    d.setActive(false);
                    d.setLastLoginAt(Instant.now()); // yoki lastLogoutAt
                    deviceRepo.save(d);
                });

        return ResponseDto.ok("Device logged out successfully");
    }


    @Transactional
    public ResponseDto<Void> logoutAll(UUID userId, String currentDeviceId) {
        refreshRepo.revokeAllExceptDevice(userId, currentDeviceId);
        revokedAccessTokenRepository.revokeAllExceptDevice(userId, currentDeviceId);
        deviceRepo.deactivateAllExcept(userId, currentDeviceId);
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
