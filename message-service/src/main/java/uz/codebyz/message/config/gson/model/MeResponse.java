package uz.codebyz.message.config.gson.model;


import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

public class MeResponse {
    private UUID id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String role;
    private boolean emailVerified;
    private boolean active;

    private String avatarUrl;
    private String avatarName;
    private Long avatarSize;
    private String avatarSizeMB;
    private String avatarFilePath;
    private SocialLinks socialLinks;
    private String birthDate;

    private String lastOnline;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
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

    public String getAvatarFilePath() {
        return avatarFilePath;
    }

    public void setAvatarFilePath(String avatarFilePath) {
        this.avatarFilePath = avatarFilePath;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public SocialLinks getSocialLinks() {
        return socialLinks;
    }

    public void setSocialLinks(SocialLinks socialLinks) {
        this.socialLinks = socialLinks;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public static class SocialLinks {
        private String telegram;
        private String email;
        private String instagram;
        private String facebook;
        private String linkedin;
        private String twitter;
        private String tiktok;
        private String github;
        private String gitlab;
        private String stackoverflow;
        private String youtube;
        private String medium;
        private String blog;
        private String website;
        private String portfolio;
        private String resume;

        public String getTelegram() {
            return telegram;
        }

        public void setTelegram(String telegram) {
            this.telegram = telegram;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getInstagram() {
            return instagram;
        }

        public void setInstagram(String instagram) {
            this.instagram = instagram;
        }

        public String getFacebook() {
            return facebook;
        }

        public void setFacebook(String facebook) {
            this.facebook = facebook;
        }

        public String getLinkedin() {
            return linkedin;
        }

        public void setLinkedin(String linkedin) {
            this.linkedin = linkedin;
        }

        public String getTwitter() {
            return twitter;
        }

        public void setTwitter(String twitter) {
            this.twitter = twitter;
        }

        public String getTiktok() {
            return tiktok;
        }

        public void setTiktok(String tiktok) {
            this.tiktok = tiktok;
        }

        public String getGithub() {
            return github;
        }

        public void setGithub(String github) {
            this.github = github;
        }

        public String getGitlab() {
            return gitlab;
        }

        public void setGitlab(String gitlab) {
            this.gitlab = gitlab;
        }

        public String getStackoverflow() {
            return stackoverflow;
        }

        public void setStackoverflow(String stackoverflow) {
            this.stackoverflow = stackoverflow;
        }

        public String getYoutube() {
            return youtube;
        }

        public void setYoutube(String youtube) {
            this.youtube = youtube;
        }

        public String getMedium() {
            return medium;
        }

        public void setMedium(String medium) {
            this.medium = medium;
        }

        public String getBlog() {
            return blog;
        }

        public void setBlog(String blog) {
            this.blog = blog;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getPortfolio() {
            return portfolio;
        }

        public void setPortfolio(String portfolio) {
            this.portfolio = portfolio;
        }

        public String getResume() {
            return resume;
        }

        public void setResume(String resume) {
            this.resume = resume;
        }
    }
}
