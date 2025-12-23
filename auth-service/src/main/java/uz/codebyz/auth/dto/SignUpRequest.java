package uz.codebyz.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignUpRequest {
    @NotBlank @Size(min=1,max=60) private String firstname;
    @NotBlank @Size(min=1,max=60) private String lastname;
    @NotBlank @Size(min=3,max=40) private String username;
    @NotBlank @Email @Size(max=120) private String email;
    @NotBlank @Size(min=6,max=72) private String password;

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
