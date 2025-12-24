package uz.codebyz.ads.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Service
public class AuthUserClient {

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    private final String baseUrl;

    public AuthUserClient(@Value("${auth.base-url:http://localhost:8081}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean userExists(UUID userId) {
        if (userId == null) return false;
        try {
            String url = "http://localhost:8081/api/users/exists?userid=" + userId;
            System.out.println(url);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(5)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return false;
            }
            String body = response.body();
            return "true".equalsIgnoreCase(body.trim());
        } catch (Exception e) {
            return false;
        }
    }
}
