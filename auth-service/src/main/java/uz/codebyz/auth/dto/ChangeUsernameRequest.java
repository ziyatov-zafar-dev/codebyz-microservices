package uz.codebyz.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeUsernameRequest {
    @NotBlank @Size(min=3,max=40)
    private String newUsername;

    public String getNewUsername() { return newUsername; }
    public void setNewUsername(String newUsername) { this.newUsername = newUsername; }
}
