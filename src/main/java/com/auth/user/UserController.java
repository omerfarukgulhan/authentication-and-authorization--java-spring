package com.auth.user;


import com.auth.error.ApiError;
import com.auth.user.dto.UserCreate;
import com.auth.user.dto.UserDTO;
import com.auth.user.exception.ActivationNotificationException;
import com.auth.user.exception.InvalidTokenException;
import com.auth.user.exception.NotFoundException;
import com.auth.user.exception.NotUniqueEmailException;
import com.auth.utils.ApiResponse;
import com.auth.utils.GenericMessage;
import com.auth.utils.Messages;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserDTO> getUsers(Pageable page){
        return userService.getUsers(page).map(UserDTO::new);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable long id){
        return new UserDTO(userService.getUser(id));
    }



    @PostMapping
    public ApiResponse<UserCreate> createUser(@Valid @RequestBody UserCreate user) {
        userService.save(user.toUser());
        String message = Messages.getMessageForLocale("auth.create.user.success.message", LocaleContextHolder.getLocale());
        return new ApiResponse<>("success", message, user);
    }

    @PatchMapping("/{token}/active")
    public GenericMessage activateUser(@PathVariable String token) {
        userService.activateUser(token);
        String message = Messages.getMessageForLocale("auth.activate.user.success.message", LocaleContextHolder.getLocale());
        return new GenericMessage(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgNotValidEx(MethodArgumentNotValidException exception) {
        ApiError apiError = new ApiError();
        apiError.setPath("/api/v1/users");
        String message = Messages.getMessageForLocale("auth.error.validation", LocaleContextHolder.getLocale());
        apiError.setMessage(message);
        apiError.setStatus(400);
        var validationErrors = exception.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (existing, replacing) -> existing));
        apiError.setValidationErrors(validationErrors);
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(NotUniqueEmailException.class)
    public ResponseEntity<ApiError> handleNotUniqueEmailEx(NotUniqueEmailException exception) {
        ApiError apiError = new ApiError();
        apiError.setPath("/api/v1/users");
        apiError.setMessage(exception.getMessage());
        apiError.setStatus(400);
        apiError.setValidationErrors(exception.getValidationErrors());
        return ResponseEntity.status(400).body(apiError);
    }

    @ExceptionHandler(ActivationNotificationException.class)
    public ResponseEntity<ApiError> handleActivationNotificationException(ActivationNotificationException exception) {
        ApiError apiError = new ApiError();
        apiError.setPath("/api/v1/users");
        apiError.setMessage(exception.getMessage());
        apiError.setStatus(502);
        return ResponseEntity.status(502).body(apiError);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException exception, HttpServletRequest request){
        ApiError apiError = new ApiError();
        apiError.setPath("/api/v1/users");
        apiError.setMessage(exception.getMessage());
        apiError.setStatus(400);
        return ResponseEntity.status(400).body(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<ApiError> handleNotFoundException(NotFoundException exception, HttpServletRequest request){
        ApiError apiError = new ApiError();
        apiError.setPath(request.getRequestURI());
        apiError.setMessage(exception.getMessage());
        apiError.setStatus(404);
        return ResponseEntity.status(404).body(apiError);
    }
}
