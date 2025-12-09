package com.harshal.ticket_service.security;

import com.harshal.ticket_service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Aspect                // tells Spring this is an AOP aspect
@Component             // Spring bean
@RequiredArgsConstructor
public class RoleSecurityAspect {

    private final HttpServletRequest request;  // to read headers
    private final SecurityUtil securityUtil;   // our role helper

    private String getRoleFromHeader() {
        String role = request.getHeader("X-User-Role");
        if (role == null || role.isBlank()) {
            throw new ApiException("Missing role in request", HttpStatus.UNAUTHORIZED);
        }
        return role;
    }

    // runs before any method/class annotated with @AdminOnly
    @Before("@within(com.harshal.ticket_service.security.AdminOnly) || " +
            "@annotation(com.harshal.ticket_service.security.AdminOnly)")
    public void checkAdminOnly() {
        String role = getRoleFromHeader();
        securityUtil.allowAdminOnly(role);
    }

    // runs before any method/class annotated with @CustomerOnly
    @Before("@within(com.harshal.ticket_service.security.CustomerOnly) || " +
            "@annotation(com.harshal.ticket_service.security.CustomerOnly)")
    public void checkCustomerOnly() {
        String role = getRoleFromHeader();
        securityUtil.allowCustomerOnly(role);
    }

    // runs before any method/class annotated with @AdminOrStaff
    @Before("@within(com.harshal.ticket_service.security.AdminOrStaff) || " +
            "@annotation(com.harshal.ticket_service.security.AdminOrStaff)")
    public void checkAdminOrStaff() {
        String role = getRoleFromHeader();
        securityUtil.allowAdminOrStaff(role);
    }
}
