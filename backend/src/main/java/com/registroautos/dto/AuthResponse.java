package com.registroautos.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
