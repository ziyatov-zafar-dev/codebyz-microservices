package uz.codebyz.auth.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SocialLinks {
    @Column(name = "telegram_link") private String telegram;
    @Column(name = "email_link") private String email;

    @Column(name = "instagram_link") private String instagram;
    @Column(name = "facebook_link") private String facebook;
    @Column(name = "linkedin_link") private String linkedin;
    @Column(name = "twitter_link") private String twitter;
    @Column(name = "tiktok_link") private String tiktok;

    @Column(name = "github_link") private String github;
    @Column(name = "gitlab_link") private String gitlab;
    @Column(name = "stackoverflow_link") private String stackoverflow;

    @Column(name = "youtube_link") private String youtube;
    @Column(name = "medium_link") private String medium;
    @Column(name = "blog_link") private String blog;

    @Column(name = "website_link") private String website;
    @Column(name = "portfolio_link") private String portfolio;
    @Column(name = "resume_link") private String resume;

    public String getTelegram() { return telegram; }
    public void setTelegram(String telegram) { this.telegram = telegram; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }
    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
    public String getTwitter() { return twitter; }
    public void setTwitter(String twitter) { this.twitter = twitter; }
    public String getTiktok() { return tiktok; }
    public void setTiktok(String tiktok) { this.tiktok = tiktok; }
    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }
    public String getGitlab() { return gitlab; }
    public void setGitlab(String gitlab) { this.gitlab = gitlab; }
    public String getStackoverflow() { return stackoverflow; }
    public void setStackoverflow(String stackoverflow) { this.stackoverflow = stackoverflow; }
    public String getYoutube() { return youtube; }
    public void setYoutube(String youtube) { this.youtube = youtube; }
    public String getMedium() { return medium; }
    public void setMedium(String medium) { this.medium = medium; }
    public String getBlog() { return blog; }
    public void setBlog(String blog) { this.blog = blog; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
    public String getResume() { return resume; }
    public void setResume(String resume) { this.resume = resume; }
}
