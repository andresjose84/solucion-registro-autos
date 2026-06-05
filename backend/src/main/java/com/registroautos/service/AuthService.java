package com.registroautos.service;

import com.registroautos.dto.AuthResponse;
import com.registroautos.dto.LoginRequest;
import com.registroautos.dto.RegisterRequest;
import com.registroautos.dto.UserResponse;
import com.registroautos.entity.User;
import com.registroautos.exception.DuplicateResourceException;
import com.registroautos.mapper.EntityMapper;
import com.registroautos.repository.UserRepository;
import com.registroautos.security.CustomUserDetailsService;
import com.registroautos.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email().trim().toLowerCase())) {
            throw new DuplicateResourceException("El email ya esta registrado");
        }

        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());

        User saved = userRepository.save(user);
        return EntityMapper.toUserResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().trim().toLowerCase(),
                        request.password()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email().trim().toLowerCase());
        User user = userDetailsService.loadEntityByEmail(userDetails.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, EntityMapper.toUserResponse(user));
    }
}
