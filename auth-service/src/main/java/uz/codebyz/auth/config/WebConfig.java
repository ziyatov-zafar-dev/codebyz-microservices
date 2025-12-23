package uz.codebyz.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final String uploadsDir;

    public WebConfig(@Value("${storage.uploads-dir}") String uploadsDir) {
        this.uploadsDir = Paths.get(uploadsDir).toAbsolutePath().normalize().toString();
        try {
            java.nio.file.Files.createDirectories(Paths.get(this.uploadsDir));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Auth service uploads papkasini yaratishda xatolik", e);
        }
        System.out.println("Auth Service Uploads Directory: " + this.uploadsDir);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + uploadsDir + "/";
        registry.addResourceHandler("/odnlicasjocdiahduhjcoinaurofrejdhiudosjkhfddddddddddddddddddddiopasdijkhieodfjhsiui0eodjifhureodihuosfdjfiles/**")
                .addResourceLocations(location);

        System.out.println("Auth Service - File URLs: http://localhost:8081/odnlicasjocdiahduhjcoinaurofrejdhiudosjkhfddddddddddddddddddddiopasdijkhieodfjhsiui0eodjifhureodihuosfdjfiles/{filename}");
    }
}