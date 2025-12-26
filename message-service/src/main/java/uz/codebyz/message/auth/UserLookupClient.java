package uz.codebyz.message.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import uz.codebyz.message.exception.BadRequestException;

import java.util.UUID;

@Component
public class UserLookupClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserLookupClient(RestTemplate restTemplate,
                            @Value("${auth.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public boolean exists(UUID userId) {
        String url = baseUrl + "/api/users/exists/" + userId;
        try {
            Boolean response = restTemplate.getForObject(url, Boolean.class);
            if (response == null) {
                throw new BadRequestException("Auth servisi noaniq javob qaytardi");
            }
            return response;
        } catch (RestClientException ex) {
            throw new BadRequestException("Auth servisiga ulanib bo'lmadi");
        }
    }
}
