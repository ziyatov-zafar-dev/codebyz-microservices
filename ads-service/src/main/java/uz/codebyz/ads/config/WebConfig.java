package uz.codebyz.ads.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${storage.uploads-dir:uploads}")
    private String uploadsDir;

    @Value("${storage.ad.public-url}")
    private String publicUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path normalized = Paths.get(uploadsDir).toAbsolutePath().normalize();
        String location = "file:" + normalized.toString() + "/";
        String pathPattern = StringUtils.trimTrailingCharacter(publicUrl, '/') + "/**";
        registry.addResourceHandler(pathPattern).addResourceLocations(location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
