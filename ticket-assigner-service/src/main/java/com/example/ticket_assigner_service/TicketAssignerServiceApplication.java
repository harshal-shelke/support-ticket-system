package com.example.ticket_assigner_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class TicketAssignerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketAssignerServiceApplication.class, args);
	}

}
