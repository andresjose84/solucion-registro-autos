package com.registroautos.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-jwt-secret-key-with-at-least-32-bytes!!", 3_600_000L);
        userDetails = User.builder()
                .username("demo@registroautos.com")
                .password("secret")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @Test
    void generateToken_shouldReturnNonEmptyToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
    }

    @Test
    void extractUsername_shouldReturnSubjectFromToken() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.extractUsername(token)).isEqualTo("demo@registroautos.com");
    }

    @Test
    void isTokenValid_shouldReturnTrueForMatchingUser() {
        String token = jwtService.generateToken(userDetails);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = User.builder()
                .username("other@example.com")
                .password("secret")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void constructor_shouldRejectShortSecret() {
        assertThatThrownBy(() -> new JwtService("short-secret", 3600000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("JWT secret must be at least 32 bytes");
    }
}
