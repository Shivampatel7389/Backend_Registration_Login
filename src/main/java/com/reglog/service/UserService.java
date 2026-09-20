package com.reglog.service;

import com.reglog.dto.MessageResponse;
import com.reglog.dto.RegisterRequest;
import com.reglog.dto.UserResponse;
import com.reglog.entity.User;
import com.reglog.exception.AppExceptions;
import com.reglog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MessageResponse registerUser(RegisterRequest request) {
        // 1. Check existing email
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new AppExceptions.DuplicateEmailException("Email address is already in use. Please use another email or login.");
        }

        // 2. Check existing username
        if (userRepository.existsByName(request.getName().trim())) {
            throw new AppExceptions.BadRequestException("Username is already taken. Please choose another username.");
        }

        // 3. Hash password using BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 4. Create and save User
        User user = new User(
                null,
                request.getName().trim(),
                hashedPassword,
                request.getEmail().trim().toLowerCase(),
                request.getPhone().trim()
        );

        userRepository.save(user);

        return new MessageResponse("User registered successfully");
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(String username) {
        User user = userRepository.findByName(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("User profile not found for: " + username));

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }
}
