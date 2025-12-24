package uz.codebyz.auth.dto;

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
}
