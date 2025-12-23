package uz.codebyz.message.config.gson.model;

import uz.codebyz.auth.user.SocialLinks;
import uz.codebyz.auth.user.UserRole;

import java.time.Instant;
import java.time.ZonedDateTime;
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

    private Instant birthDate;
    private SocialLinks socialLinks;
    private ZonedDateTime lastOnline;

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

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
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

    private Instant uploadedImgTime;

    public Instant getUploadedImgTime() {
        return uploadedImgTime;
    }

    public void setUploadedImgTime(Instant uploadedImgTime) {
        this.uploadedImgTime = uploadedImgTime;
    }

    public void setAvatarFilePath(String avatarFilePath) {
        this.avatarFilePath = avatarFilePath;
    }

    public ZonedDateTime getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(ZonedDateTime lastOnline) {
        this.lastOnline = lastOnline;
    }
}
