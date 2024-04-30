package com.server.user;

import java.util.UUID;

import com.server.config.CurrentUser;
import com.server.email.EmailService;
import com.server.file.FileService;
import com.server.user.dto.PasswordResetRequest;
import com.server.user.dto.PasswordUpdate;
import com.server.user.dto.UserUpdate;
import com.server.user.exception.ActivationNotificationException;
import com.server.user.exception.InvalidTokenException;
import com.server.user.exception.NotFoundException;
import com.server.user.exception.NotUniqueEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final FileService fileService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.fileService = fileService;
    }

    @Transactional(rollbackOn = MailException.class)
    public void save(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActivationToken(UUID.randomUUID().toString());
            userRepository.saveAndFlush(user);
            emailService.sendActivationEmail(user.getEmail(), user.getActivationToken());
        } catch (DataIntegrityViolationException ex) {
            throw new NotUniqueEmailException();
        } catch (MailException ex) {
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

    public Page<User> getUsers(Pageable page, CurrentUser currentUser) {
        if (currentUser == null) {
            return userRepository.findAll(page);
        }
        return userRepository.findByIdNot(currentUser.getId(), page);
    }

    public User getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(long id, UserUpdate userUpdate) {
        User user = getUser(id);
        user.setUsername(userUpdate.username());
        System.out.println(userUpdate.image());
        if (userUpdate.image() != null) {
            String fileName = fileService.saveBase64StringAsFile(userUpdate.image());
            fileService.deleteProfileImage(user.getImage());
            user.setImage(fileName);
        }
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        User user = getUser(id);
        if (user.getImage() != null) {
            fileService.deleteProfileImage(user.getImage());
        }
        userRepository.delete(user);
    }

    public void handleResetRequest(PasswordResetRequest passwordResetRequest) {
        User user = findByEmail(passwordResetRequest.email());
        if (user == null) throw new NotFoundException(0);
        user.setPasswordResetToken(UUID.randomUUID().toString());
        this.userRepository.save(user);
        this.emailService.sendPasswordResetEmail(user.getEmail(), user.getPasswordResetToken());
    }

    public void updatePassword(String token, PasswordUpdate passwordUpdate) {
        User user = userRepository.findByPasswordResetToken(token);
        if (user == null) {
            throw new InvalidTokenException();
        }
        user.setPasswordResetToken(null);
        user.setPassword(passwordEncoder.encode(passwordUpdate.password()));
        user.setActive(true);
        userRepository.save(user);
    }
}