package uz.codebyz.message.service.impl;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import uz.codebyz.message.config.gson.InstantTypeAdapter;
import uz.codebyz.message.config.gson.ZonedDateTimeTypeAdapter;

import uz.codebyz.message.config.gson.model.MeResponse;
import uz.codebyz.message.service.UserService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson;

    public UserServiceImpl() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .create();
    }
    @Override
    public boolean userExists(UUID userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:8081/api/users/exists/" + userId
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AuthenticationServiceException(
                        "Auth service returned status " + response.statusCode()
                );
            }

            // auth-service plain Boolean qaytaryapti: true / false
            Boolean exists = gson.fromJson(response.body(), Boolean.class);
            return Boolean.TRUE.equals(exists);

        } catch (Exception e) {
            throw new AuthenticationServiceException(
                    "Auth service error: " + e.getMessage()
            );
        }
    }

    @Override
    public MeResponse getUser(UUID userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(
                            "http://localhost:8081/api/users/exists/user/" + userId
                    ))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AuthenticationServiceException(
                        "Auth service returned status " + response.statusCode()
                );
            }

            return gson.fromJson(response.body(), MeResponse.class);

        } catch (Exception e) {
            throw new AuthenticationServiceException(
                    "Auth service error: " + e.getMessage()
            );
        }
    }
}
