package uz.codebyz.auth.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
@Configuration
public class TimeZoneConfig {

    @Value("${app.timezone}")
    private String timeZone;

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
        System.out.println("✅ JVM TimeZone set to: " + TimeZone.getDefault().getID());
    }
}
