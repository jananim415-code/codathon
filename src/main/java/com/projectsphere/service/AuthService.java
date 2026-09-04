package com.projectsphere.service;

import com.projectsphere.dto.*;
import com.projectsphere.entity.User;
import com.projectsphere.exception.ConflictException;
import com.projectsphere.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projectsphere.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final com.projectsphere.security.ProjectSphereUserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       com.projectsphere.security.ProjectSphereUserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setGithubUsername(request.githubUsername());
        user.setRole(User.Role.STUDENT);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
        return tokenResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthLoginRequest request) {
        String email = request.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new IllegalStateException("Authenticated user no longer exists"));
        return tokenResponse(user);
    }

    private AuthResponse tokenResponse(User user) {
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        return new AuthResponse(jwtService.generateToken(details), "Bearer",
            jwtService.getExpirationMs() / 1000, EntityDtoMapper.toResponse(user));
    }
}
