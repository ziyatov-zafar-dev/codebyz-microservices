package uz.codebyz.auth.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class IpWhoIsClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Gson gson = new GsonBuilder().create();

    public IpWhoIsResponse lookup(String ip) {
        if (ip == null || ip.isBlank()) return null;
        try {
            String url = "https://ipwho.is/" + ip;

            ResponseEntity<String> response =
                    restTemplate.getForEntity(url, String.class);

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null) {
                return null;
            }

            IpWhoIsResponse result =
                    gson.fromJson(response.getBody(), IpWhoIsResponse.class);

            if (result == null || !result.isSuccess()) {
                return null;
            }

            return result;

        } catch (RestClientException ex) {
            return null;
        }
    }
}
