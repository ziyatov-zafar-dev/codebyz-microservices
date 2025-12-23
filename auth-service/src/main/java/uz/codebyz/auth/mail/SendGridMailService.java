package uz.codebyz.auth.mail;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;
import uz.codebyz.auth.config.properties.SendGridProperties;

import java.io.IOException;

@Service
public class SendGridMailService implements MailService {

    private final SendGridProperties props;

    public SendGridMailService(SendGridProperties props) {
        this.props = props;
    }

    @Override
    public void sendVerificationCode(String toEmail, String subject, String code, String purpose) {
        if (!props.isEnabled()) return;
        if (props.getApiKey() == null || props.getApiKey().isBlank()) return;

        Email from = new Email(props.getFromEmail(), props.getFromName());
        Email to = new Email(toEmail);

        String html = "<div style='font-family:Arial,sans-serif;line-height:1.5'>"
                + "<h2>CodeByZ Verification</h2>"
                + "<p>Purpose: <b>" + purpose + "</b></p>"
                + "<p>Your verification code:</p>"
                + "<div style='font-size:28px;font-weight:700;letter-spacing:4px;'>" + code + "</div>"
                + "</div>";

        Content content = new Content("text/html", html);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(props.getApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sg.api(request);
        } catch (IOException ex) {
            throw new RuntimeException("SendGrid error: " + ex.getMessage(), ex);
        }
    }
}
