package com.example.ticket_assigner_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StaffClient {

    private final RestTemplate restTemplate;

    // URL of AUTH SERVICE (local for now – later via gateway)
    private static final String AUTH_SERVICE_URL = "http://localhost:8081/admin/users/staff";

    public List<Map<String, Object>> fetchStaffList() {
        List<Map<String, Object>> staffList =
                restTemplate.getForObject(AUTH_SERVICE_URL, List.class);

        return staffList;
    }
}
