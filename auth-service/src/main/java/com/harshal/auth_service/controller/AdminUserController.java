package com.harshal.auth_service.controller;

import com.harshal.auth_service.entity.User;
import com.harshal.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AuthService userService;

    // ADMIN ONLY: change role
    @PatchMapping("/change-role")
    public ResponseEntity<String> changeRole(
            @RequestParam String email,
            @RequestParam String role,
            @RequestHeader("X-User-Role") String userRole
    ) {
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only ADMIN can change roles");
        }

        return ResponseEntity.ok(userService.changeRole(email, role));
    }

    // ADMIN ONLY: disable user
    @PatchMapping("/disable")
    public ResponseEntity<String> disableUser(
            @RequestParam String email,
            @RequestHeader("X-User-Role") String userRole
    ) {
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only ADMIN can disable users");
        }

        return ResponseEntity.ok(userService.disableUser(email));
    }

    // ADMIN ONLY: enable user
    @PatchMapping("/enable")
    public ResponseEntity<String> enableUser(
            @RequestParam String email,
            @RequestHeader("X-User-Role") String userRole
    ) {
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only ADMIN can enable users");
        }

        return ResponseEntity.ok(userService.enableUser(email));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<User>> getAllStaffUsers(
            @RequestHeader("X-User-Role") String requesterRole
    ) {
        if (!"ADMIN".equalsIgnoreCase(requesterRole)) {
            return ResponseEntity.status(403).build();
        }

        List<User> staffUsers = userService.getAllStaff();
        return ResponseEntity.ok(staffUsers);
    }

}
