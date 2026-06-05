package com.registroautos.dto;

import java.time.LocalDateTime;

public record CarResponse(
        Long id,
        String brand,
        String model,
        Integer year,
        String plate,
        String color,
        String photoUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
