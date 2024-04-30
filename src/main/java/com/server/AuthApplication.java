package com.server;

import com.server.user.User;
import com.server.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    @Bean
    @Profile("dev")
    CommandLineRunner userCreator(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            for (var i = 1; i <= 25; i++) {
                User user = new User();
                user.setUsername("user" + i);
                user.setEmail("user" + i + "@mail.com");
                user.setPassword(passwordEncoder.encode("P4ssword"));
                user.setActive(i != 1);
                userRepository.save(user);
            }
        };
    }
}
