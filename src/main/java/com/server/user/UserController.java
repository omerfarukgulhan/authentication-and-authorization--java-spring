package com.server.user;

import com.server.config.CurrentUser;
import com.server.user.dto.*;
import com.server.utils.GenericMessage;
import com.server.utils.Messages;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserDTO> getUsers(Pageable page, @AuthenticationPrincipal CurrentUser currentUser) {
        return userService.getUsers(page, currentUser).map(UserDTO::new);
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable long id) {
        return new UserDTO(userService.getUser(id));
    }

    @PostMapping
    public GenericMessage createUser(@Valid @RequestBody UserCreate user) {
        userService.save(user.toUser());
        String message = Messages.getMessageForLocale("auth.create.user.success.message", LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == principal.id")
    UserDTO updateUser(@PathVariable long id, @Valid @RequestBody UserUpdate userUpdate) {
        return new UserDTO(userService.updateUser(id, userUpdate));
    }

    @PatchMapping("/{token}/active")
    public GenericMessage activateUser(@PathVariable String token) {
        userService.activateUser(token);
        String message = Messages.getMessageForLocale("auth.activate.user.success.message", LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == principal.id")
    GenericMessage deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return new GenericMessage("User is deleted");
    }

    @PostMapping("/password-reset")
    GenericMessage passwordResetRequest(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        userService.handleResetRequest(passwordResetRequest);
        return new GenericMessage("Check your email address to reset your password");
    }

    @PatchMapping("/{token}/password")
    GenericMessage setPassword(@PathVariable String token, @Valid @RequestBody PasswordUpdate passwordUpdate) {
        userService.updatePassword(token, passwordUpdate);
        return new GenericMessage("Password updated successfully");
    }
}
