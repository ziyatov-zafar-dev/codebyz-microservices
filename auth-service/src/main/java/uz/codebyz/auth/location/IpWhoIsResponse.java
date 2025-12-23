package uz.codebyz.auth.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IpWhoIsResponse {
    private boolean success;
    private String ip;
    private String country;
    private String region;
    private String city;
    private String timezone;
    private String isp;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

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
}
