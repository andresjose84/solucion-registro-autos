package com.registroautos.dto;

import java.time.LocalDateTime;

public record HealthResponse(
        String status,
        String service,
        LocalDateTime timestamp,
        DatabaseHealth database
) {
    public record DatabaseHealth(String status) {
    }
}
