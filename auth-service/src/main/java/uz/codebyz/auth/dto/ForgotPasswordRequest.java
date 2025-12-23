package uz.codebyz.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "Email or username is required")
    private String identifier; // email YOKI username

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
