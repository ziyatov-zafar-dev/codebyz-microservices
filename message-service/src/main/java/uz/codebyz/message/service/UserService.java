package uz.codebyz.message.service;



import uz.codebyz.message.config.gson.model.MeResponse;

import java.util.UUID;

public interface UserService {
     boolean userExists(UUID userId);
     MeResponse getUser(UUID userId);
}
