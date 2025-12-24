package uz.codebyz.auth.device;

import jakarta.persistence.*;
import uz.codebyz.auth.device.enums.DeviceType;
import uz.codebyz.auth.location.Timezone;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_devices")
public class UserDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;
    private String browserName;    // Chrome, Firefox, Edge
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;
    private String browserVersion; // 120.0.6099.71
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(nullable = false, length = 128)
    private String deviceId;

    @Column(length = 300)
    private String userAgent;
    @Column(length = 60)
    private String ip;


    @Column(nullable = false)
    private boolean active;
    @Column(length = 120)
    private String deviceName;


    @Column(nullable = false)
    private Instant lastLoginAt = Instant.now();

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public UserDevice(String browserName, DeviceType deviceType, String browserVersion, UUID userId, String deviceId, String userAgent, String ip, boolean active, String deviceName, Instant lastLoginAt, Instant createdAt) {
        this.browserName = browserName;
        this.deviceType = deviceType;
        this.browserVersion = browserVersion;
        this.userId = userId;
        this.deviceId = deviceId;
        this.userAgent = userAgent;
        this.ip = ip;
        this.active = active;
        this.deviceName = deviceName;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }

    public UserDevice() {

    }

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) createdAt = now;
        if (lastLoginAt == null) lastLoginAt = now;
        active = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }
}
