package uz.codebyz.message.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String uploadsDir;

    public WebConfig(@Value("${storage.uploads-dir:uploads}") String uploadsDir) {
        Path normalized = Paths.get(uploadsDir).toAbsolutePath().normalize();
        this.uploadsDir = normalized.toString();
        try {
            java.nio.file.Files.createDirectories(normalized);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Uploads directory creation failed", e);
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*").allowedMethods("*").allowedHeaders("*").allowCredentials(false);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadsDir + "/";
        registry.addResourceHandler("/odnlicasjocdiahduhjcoinaurofrejdhiudosjkhfddddddddddddddddddddiopasdijkhieodfjhsiui0eodjifhureodihuosfdjfiles/**").addResourceLocations(location);
    }
}
