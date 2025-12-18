package com.harshal.auth_service.service;

import com.harshal.auth_service.config.JwtService;
import com.harshal.auth_service.dto.AuthResponse;
import com.harshal.auth_service.dto.LoginRequest;
import com.harshal.auth_service.dto.RegisterRequest;
import com.harshal.auth_service.entity.User;
import com.harshal.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public AuthResponse register(RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse("Email already exists", null);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole("CUSTOMER");
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse("User registered successfully", token);
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty()) {
            return new AuthResponse("Invalid Email or Password", null);
        }

        if (!user.get().isActive()) {
            return new AuthResponse("Your account has been disabled by ADMIN", null);
        }

        boolean matches = passwordEncoder.matches(request.getPassword(), user.get().getPassword());

        if (!matches) {
            return new AuthResponse("Password is incorrect", null);
        }

        String token = jwtService.generateToken(user.get().getEmail());
        return new AuthResponse("Login successful", token);
    }


    // ---------------- ADMIN METHODS ----------------

    public String changeRole(String email, String newRole) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return "User not found";
        }

        user.get().setRole(newRole);
        userRepository.save(user.get());
        return "Role updated successfully";
    }

    public String disableUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) return "User not found";

        user.get().setActive(false);
        userRepository.save(user.get());
        return "User disabled";
    }

    public String enableUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) return "User not found";

        user.get().setActive(true);
        userRepository.save(user.get());
        return "User enabled";
    }

    public List<User> getAllStaffUsers() {
        return userRepository.findByRoleAndActive("STAFF", true);
    }

    public List<User> getAllStaff() {
        return userRepository.findByRole("STAFF");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
