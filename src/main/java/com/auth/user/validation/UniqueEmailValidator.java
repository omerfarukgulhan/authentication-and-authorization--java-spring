package com.auth.user.validation;

import com.auth.user.User;
import com.auth.user.UserRepository;
import jakarta.validation.ConstraintValidator;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final UserRepository userRepository;

    @Autowired
    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        User user = userRepository.findByEmail(value);
        return user == null;
    }

}