package com.registroautos.dto;

public record UserResponse(
        Long id,
        String email,
        String fullName
) {
}
