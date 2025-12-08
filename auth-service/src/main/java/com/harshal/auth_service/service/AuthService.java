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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();

    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            return new AuthResponse("Email is Already Present",null);
        }
        User user=new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse("User Registered Successsfully",token);
    }

    public AuthResponse login(LoginRequest request){
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if(user.isEmpty())return new AuthResponse("Invalid Email or Password",null);

        boolean matches = passwordEncoder.matches(request.getPassword(), user.get().getPassword());

        if(!matches){
            return new AuthResponse("Password is Incorrect",null);
        }

        String token = jwtService.generateToken(user.get().getEmail());
        return new AuthResponse("Login Successful",token);
    }
}
