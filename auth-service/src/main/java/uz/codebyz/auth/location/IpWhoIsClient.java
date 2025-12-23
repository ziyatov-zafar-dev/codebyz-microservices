package uz.codebyz.auth.location;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class IpWhoIsClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public IpWhoIsResponse lookup(String ip) {
        if (ip == null || ip.isBlank()) return null;
        try {
            ResponseEntity<IpWhoIsResponse> resp = restTemplate.getForEntity("https://ipwho.is/" + ip, IpWhoIsResponse.class);
            return resp.getBody();
        } catch (RestClientException ex) {
            return null;
        }
    }
}
