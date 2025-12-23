package uz.codebyz.auth.dto;

public class AuthTokensResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public AuthTokensResponse() {
    }

    public AuthTokensResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
        this.active = true;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
