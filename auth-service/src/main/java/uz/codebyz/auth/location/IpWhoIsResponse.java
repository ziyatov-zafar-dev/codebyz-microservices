package uz.codebyz.auth.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nimbusds.jose.shaded.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IpWhoIsResponse {
    private boolean success;
    private String ip;
    private String type;
    private String continent;
    private String country;
    private String country_code;
    private String region;
    private String city;
    private String postal;
    private Double latitude;
    private Double longitude;
    private String isp;
    private Timezone timezone;

    @SerializedName("is_eu")
    private Boolean isEu;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public Timezone getTimezone() {
        return timezone;
    }

    public void setTimezone(Timezone timezone) {
        this.timezone = timezone;
    }

    public Boolean getEu() {
        return isEu;
    }

    public void setEu(Boolean eu) {
        isEu = eu;
    }
}
