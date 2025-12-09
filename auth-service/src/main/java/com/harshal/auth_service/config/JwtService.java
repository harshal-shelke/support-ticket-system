package com.harshal.auth_service.config;

import com.harshal.auth_service.entity.User;
import com.harshal.auth_service.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;
    // 32+ char secret key (just a long random string)
    private static final String SECRET_KEY = "this_is_my_super_secret_jwt_key_12345";

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();   // subject = email
    }

    public String generateToken(String email) {
        Optional<User> user=userRepository.findByEmail(email);
        return Jwts.builder()
                .setSubject(email) // who is this token for? -> email
                .claim("role", user.get().getRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(SignatureAlgorithm.HS256, getSignKey())
                .compact();
    }

    public boolean isTokenValid(String token, String email) {
        String extractedEmail = extractEmail(token);
        return email.equals(extractedEmail) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private byte[] getSignKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes).getEncoded();
    }
}
