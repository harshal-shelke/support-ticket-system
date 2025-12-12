package com.harshal.auth_service.controller;

import com.harshal.auth_service.entity.User;
import com.harshal.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/internal")
@RequiredArgsConstructor
public class InternalUserController {

    private final AuthService authService;

    // INTERNAL: returns active staff users
    @GetMapping("/staff")
    public List<User> getInternalStaffList() {
        return authService.getAllStaffUsers();
    }
}
