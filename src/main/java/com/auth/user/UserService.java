package com.auth.user;

import com.auth.email.EmailService;
import com.auth.user.exception.ActivationNotificationException;
import com.auth.user.exception.InvalidTokenException;
import com.auth.user.exception.NotFoundException;
import com.auth.user.exception.NotUniqueEmailException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;

    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public Page<User> getUsers(Pageable page) {
        return userRepository.findAll(page);
    }

    public User getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    @Transactional(rollbackOn = MailException.class)
    public void save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActivationToken(UUID.randomUUID().toString());
            userRepository.saveAndFlush(user);
            emailService.sendActivationEmail(user.getEmail(), user.getActivationToken());
        } catch (DataIntegrityViolationException exception) {
            throw new NotUniqueEmailException();
        } catch (MailException exception) {
            throw new ActivationNotificationException();
        }
    }

    public void activateUser(String token) {
        User user = userRepository.findByActivationToken(token);
        if (user == null) {
            throw new InvalidTokenException();
        }
        user.setActive(true);
        user.setActivationToken(null);
        userRepository.save(user);
    }
}
