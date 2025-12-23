package uz.codebyz.auth.dto;

import jakarta.validation.constraints.Size;
import uz.codebyz.auth.user.SocialLinks;

import java.time.Instant;

public class UpdateProfileRequest {
    @Size(min=1,max=60) private String firstname;
    @Size(min=1,max=60) private String lastname;

    private Instant birthDate;
    private SocialLinks socialLinks;

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public Instant getBirthDate() { return birthDate; }
    public void setBirthDate(Instant birthDate) { this.birthDate = birthDate; }
    public SocialLinks getSocialLinks() { return socialLinks; }
    public void setSocialLinks(SocialLinks socialLinks) { this.socialLinks = socialLinks; }
}
