package uz.codebyz.auth.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.codebyz.auth.user.SocialLinks;
import uz.codebyz.auth.user.User;
import uz.codebyz.auth.user.UserRepository;
import uz.codebyz.auth.user.UserRole;

import java.time.Instant;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // 🔒 Agar admin allaqachon bor bo‘lsa — qayta qo‘shmaydi
        boolean exists =
                userRepository.existsByUsernameIgnoreCase("admin")
                        || userRepository.existsByEmailIgnoreCase("ziyatovzafar98@gmail.com");

        if (exists) {
            return;
        }

        SocialLinks links = new SocialLinks();
        links.setTelegram("https://t.me/codebyz");
        links.setEmail("mailto:ziyatovzafar98@gmail.com");
        links.setInstagram("https://instagram.com/codebyz");
        links.setFacebook("https://facebook.com/codebyz");
        links.setLinkedin("https://linkedin.com/in/zafariyatov");
        links.setGithub("https://github.com/zafaryt");
        links.setWebsite("https://codebyz.online");
        links.setPortfolio("https://codebyz-patform.uz");
        links.setResume("https://codebyz.online/resume.pdf");
        User admin = new User();
        admin.setFirstname("Zafar");
        admin.setLastname("Ziyatov");
        admin.setUsername("admin");
        admin.setEmail("ziyatovzafar98@gmail.com");
        admin.setPasswordHash(passwordEncoder.encode("Zafar123$"));
        admin.setRole(UserRole.ADMIN);
        admin.setEmailVerified(true);
        admin.setActive(true);
        admin.setSocialLinks(links);
        admin.setCreatedAt(Instant.now());
        admin.setUpdatedAt(Instant.now());

        userRepository.save(admin);

        System.out.println("✅ Default ADMIN user yaratildi: admin / Zafar123$");
    }
}
