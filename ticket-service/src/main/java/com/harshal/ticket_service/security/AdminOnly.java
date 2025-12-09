package com.harshal.ticket_service.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})   // can be on method or class
@Retention(RetentionPolicy.RUNTIME)              // available at runtime for AOP
@Documented
public @interface AdminOnly {
}
