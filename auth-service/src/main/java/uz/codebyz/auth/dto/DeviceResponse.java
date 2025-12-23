package uz.codebyz.auth.dto;

import java.time.Instant;

public class DeviceResponse {
    private String deviceId;
    private boolean active;
    private String ip;
    private String country;
    private String region;
    private String city;
    private String timezone;
    private String isp;
    private Instant lastLoginAt;
    private String deviceName;
    private Boolean me;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public String getIsp() { return isp; }
    public void setIsp(String isp) { this.isp = isp; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public Boolean getMe() {
        return me;
    }

    public void setMe(Boolean me) {
        this.me = me;
    }
}
