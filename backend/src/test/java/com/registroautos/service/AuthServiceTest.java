package com.registroautos.service;

import com.registroautos.dto.AuthResponse;
import com.registroautos.dto.LoginRequest;
import com.registroautos.dto.RegisterRequest;
import com.registroautos.dto.UserResponse;
import com.registroautos.entity.User;
import com.registroautos.exception.DuplicateResourceException;
import com.registroautos.repository.UserRepository;
import com.registroautos.security.CustomUserDetailsService;
import com.registroautos.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    private JwtService jwtService;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-jwt-secret-key-with-at-least-32-bytes!!", 3_600_000L);
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                userDetailsService
        );
    }

    @Test
    void register_shouldPersistUserAndReturnResponse() {
        RegisterRequest request = new RegisterRequest(" Juan Perez ", " Demo@RegistroAutos.com ", "Password1");

        when(userRepository.existsByEmail("demo@registroautos.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("demo@registroautos.com");
        savedUser.setFullName("Juan Perez");
        savedUser.setPassword("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = authService.register(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("demo@registroautos.com");
        assertThat(response.fullName()).isEqualTo("Juan Perez");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getEmail()).isEqualTo("demo@registroautos.com");
        assertThat(userCaptor.getValue().getFullName()).isEqualTo("Juan Perez");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("Juan Perez", "demo@registroautos.com", "Password1");
        when(userRepository.existsByEmail("demo@registroautos.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("El email ya esta registrado");
    }

    @Test
    void login_shouldAuthenticateAndReturnToken() {
        LoginRequest request = new LoginRequest(" Demo@RegistroAutos.com ", "Password1");

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("demo@registroautos.com")
                .password("encoded-password")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        User user = new User();
        user.setId(1L);
        user.setEmail("demo@registroautos.com");
        user.setFullName("Usuario Demo");

        when(userDetailsService.loadUserByUsername("demo@registroautos.com")).thenReturn(userDetails);
        when(userDetailsService.loadEntityByEmail("demo@registroautos.com")).thenReturn(user);

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("demo@registroautos.com", "Password1")
        );
        assertThat(response.token()).isNotBlank();
        assertThat(response.user().email()).isEqualTo("demo@registroautos.com");
    }
}
