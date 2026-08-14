package com.todo.manager.todoapplication.controller;

import com.todo.manager.todoapplication.entity.User;
import com.todo.manager.todoapplication.repository.UserRepository;
import com.todo.manager.todoapplication.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import com.todo.manager.todoapplication.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            return "Username already exists";
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return "User registered successfully";
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
        System.out.println("Username: " + request.getUsername());
        System.out.println("User found: " + (user != null));
        System.out.println("Password matches: " +
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(token);
    }
}