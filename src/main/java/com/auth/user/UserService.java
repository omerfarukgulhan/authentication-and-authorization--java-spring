package com.auth.user;

import com.auth.user.exception.NotUniqueEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActivationToken(UUID.randomUUID().toString());
            userRepository.save(user);
            sendActivationEmail(user);
            return user;
        } catch (DataIntegrityViolationException exception) {
            throw new NotUniqueEmailException();
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    private void sendActivationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@app.com");
        message.setTo(user.getEmail());
        message.setSubject("Account activation");
        message.setText("http://localhost:5173/activation/" + user.getActivationToken());

        getJavaMailSender().send(message);
    }

    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.ethereal.email");
        mailSender.setPort(587);

        mailSender.setUsername("juwan46@ethereal.email");
        mailSender.setPassword("SZeMu1bCxTsRESADte");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.starttls.enable", "true");
        return mailSender;
    }
}
