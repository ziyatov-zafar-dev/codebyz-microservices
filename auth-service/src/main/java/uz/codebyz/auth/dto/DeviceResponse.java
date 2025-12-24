package uz.codebyz.auth.dto;

import uz.codebyz.auth.device.enums.DeviceType;
import uz.codebyz.auth.location.AddressResponse;
import uz.codebyz.auth.location.IpWhoIsResponse;
import uz.codebyz.auth.location.Timezone;

import java.time.Instant;

public class DeviceResponse {
    private String deviceId;
    private boolean active;
    private String ip;
    private IpWhoIsResponse location;
    private Instant lastLoginAt;
    private String deviceName;
    private Boolean me;
    private String browserName;    // Chrome, Firefox, Edge
    private String userAgent;
    private DeviceType deviceType;
    private String browserVersion; // 120.0.6099.71
    private AddressResponse deviceAddress;

    public AddressResponse getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(AddressResponse deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Boolean getMe() {
        return me;
    }

    public void setMe(Boolean me) {
        this.me = me;
    }

    public IpWhoIsResponse getLocation() {
        return location;
    }

    public void setLocation(IpWhoIsResponse location) {
        this.location = location;
    }

    public DeviceResponse(String deviceId, boolean active, String ip, IpWhoIsResponse location,
                          Instant lastLoginAt, String deviceName, Boolean me,
                          String browserName, String userAgent, DeviceType deviceType,
                          String browserVersion, AddressResponse deviceAddress) {
        this.deviceId = deviceId;
        this.active = active;
        this.ip = ip;
        this.location = location;
        this.lastLoginAt = lastLoginAt;
        this.deviceName = deviceName;
        this.me = me;
        this.browserName = browserName;
        this.userAgent = userAgent;
        this.deviceType = deviceType;
        this.browserVersion = browserVersion;
        this.deviceAddress = deviceAddress;
    }
}
