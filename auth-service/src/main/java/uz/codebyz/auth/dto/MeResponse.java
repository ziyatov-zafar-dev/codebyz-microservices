package uz.codebyz.auth.dto;

import uz.codebyz.auth.user.UserRole;
import uz.codebyz.auth.user.SocialLinks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class MeResponse {
    private UUID id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private UserRole role;
    private boolean emailVerified;
    private boolean active;

    private String avatarUrl;
    private String avatarName;
    private Long avatarSize;
    private String avatarSizeMB;
    private String avatarFilePath;

    private LocalDate birthDate;
    private SocialLinks socialLinks;
    private LocalDateTime lastOnline;

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }



    private Boolean online;
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public Long getAvatarSize() {
        return avatarSize;
    }

    public void setAvatarSize(Long avatarSize) {
        this.avatarSize = avatarSize;
    }

    public String getAvatarSizeMB() {
        return avatarSizeMB;
    }

    public void setAvatarSizeMB(String avatarSizeMB) {
        this.avatarSizeMB = avatarSizeMB;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public SocialLinks getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinks socialLinks) {
        this.socialLinks = socialLinks;
    }

    public String getAvatarFilePath() {
        return avatarFilePath;
    }

    private LocalDateTime uploadedImgTime;

    public LocalDateTime getUploadedImgTime() {
        return uploadedImgTime;
    }

    public void setUploadedImgTime(LocalDateTime uploadedImgTime) {
        this.uploadedImgTime = uploadedImgTime;
    }

    public void setAvatarFilePath(String avatarFilePath) {
        this.avatarFilePath = avatarFilePath;
    }

    public LocalDateTime getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(LocalDateTime lastOnline) {
        this.lastOnline = lastOnline;
    }
}
