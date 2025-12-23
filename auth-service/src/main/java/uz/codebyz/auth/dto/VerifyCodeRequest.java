package uz.codebyz.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VerifyCodeRequest {
    @NotBlank private String identifier;
    @NotBlank @Size(min=4,max=12) private String code;

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
