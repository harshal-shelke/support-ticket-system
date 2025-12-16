package com.harshal.ticket_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureFlagConfig {

    @Value("${features.kafka-enabled:true}")
    private boolean kafkaEnabled;

    public boolean isKafkaEnabled() {
        return kafkaEnabled;
    }
}
