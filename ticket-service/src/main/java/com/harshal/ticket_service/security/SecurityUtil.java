package com.harshal.ticket_service.security;

import com.harshal.ticket_service.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public boolean isAdmin(String role) {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isStaff(String role) {
        return "STAFF".equalsIgnoreCase(role);
    }

    public boolean isCustomer(String role) {
        return "CUSTOMER".equalsIgnoreCase(role);
    }

    // throw 403 if not admin
    public void allowAdminOnly(String role) {
        if (!isAdmin(role)) {
            throw new ApiException("Access denied: Admin role required", HttpStatus.FORBIDDEN);
        }
    }

    // throw 403 if not staff
    public void allowStaffOnly(String role) {
        if (!isStaff(role)) {
            throw new ApiException("Access denied: Staff role required", HttpStatus.FORBIDDEN);
        }
    }

    // throw 403 if not customer
    public void allowCustomerOnly(String role) {
        if (!isCustomer(role)) {
            throw new ApiException("Access denied: Customer role required", HttpStatus.FORBIDDEN);
        }
    }

    // throw 403 if neither admin nor staff
    public void allowAdminOrStaff(String role) {
        if (!isAdmin(role) && !isStaff(role)) {
            throw new ApiException("Access denied: Admin or Staff role required", HttpStatus.FORBIDDEN);
        }
    }

}
