package uz.codebyz.auth.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uz.codebyz.auth.dto.DeviceResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

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

    public AddressResponse getAddress(Double latitude, Double longitude, String lang) {
        String urlPath = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon="
                + longitude + "&zoom=18&addressdetails=1&accept-language=" + lang.toLowerCase();
        Gson gson = new Gson();
        URL url;
        URLConnection connection;
        BufferedReader reader = null;
        try {
            url = new URL(urlPath);
            String ur = "%s";
            connection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String json = "", line;
            while ((line = reader.readLine()) != null)
                json = json.concat(line);
            return gson.fromJson(json, AddressResponse.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Error closing reader: " + e);
                }
            }
        }
        return null;
    }
}
