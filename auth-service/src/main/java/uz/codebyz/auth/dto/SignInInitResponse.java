package uz.codebyz.auth.dto;

public class SignInInitResponse {
    private String message;
    private String emailMasked;

    public SignInInitResponse() {
    }

    public SignInInitResponse(String message, String emailMasked) {
        this.message = message;
        this.emailMasked = emailMasked;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmailMasked() {
        return emailMasked;
    }

    public void setEmailMasked(String emailMasked) {
        this.emailMasked = emailMasked;
    }
}
