package uz.codebyz.auth.mail;

public interface MailService {
    void sendVerificationCode(String toEmail, String subject, String code, String purpose);
}
