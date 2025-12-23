package uz.codebyz.user.rest;

import org.springframework.web.bind.annotation.*;
import uz.codebyz.user.config.User;

import java.util.List;

@RestController
@RequestMapping("/api/users-test")
public class UserTestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from USER-SERVICE 👋";
    }

    @GetMapping("/info")
    public Object info() {
        return new User(1L, "Zafar", "USER");
    }

}
