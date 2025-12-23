package uz.codebyz.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verification")
public class VerificationProperties {
    private long signUpCodeMinutes;
    private long signInCodeMinutes;
    private Password password = new Password();

    public long getSignUpCodeMinutes() {
        return signUpCodeMinutes;
    }

    public void setSignUpCodeMinutes(long signUpCodeMinutes) {
        this.signUpCodeMinutes = signUpCodeMinutes;
    }

    public long getSignInCodeMinutes() {
        return signInCodeMinutes;
    }

    public void setSignInCodeMinutes(long signInCodeMinutes) {
        this.signInCodeMinutes = signInCodeMinutes;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public static class Password {

        private long forgotPasswordCodeMinutes;
        private long changePasswordCodeMinutes;

        public long getForgotPasswordCodeMinutes() {
            return forgotPasswordCodeMinutes;
        }

        public void setForgotPasswordCodeMinutes(long forgotPasswordCodeMinutes) {
            this.forgotPasswordCodeMinutes = forgotPasswordCodeMinutes;
        }

        public long getChangePasswordCodeMinutes() {
            return changePasswordCodeMinutes;
        }

        public void setChangePasswordCodeMinutes(long changePasswordCodeMinutes) {
            this.changePasswordCodeMinutes = changePasswordCodeMinutes;
        }
    }
}
