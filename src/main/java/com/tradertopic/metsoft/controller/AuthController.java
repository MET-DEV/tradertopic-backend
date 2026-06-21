package com.tradertopic.metsoft.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import com.tradertopic.metsoft.entity.dto.AuthResponseDto;
import com.tradertopic.metsoft.entity.dto.LoginRequestDto;
import com.tradertopic.metsoft.entity.dto.RegisterRequestDto;
import com.tradertopic.metsoft.entity.model.auth.AppUser;
import com.tradertopic.metsoft.entity.model.auth.Role;
import com.tradertopic.metsoft.repository.AppUserRepository;
import com.tradertopic.metsoft.repository.RoleRepository;
import com.tradertopic.metsoft.services.auth.JwtUtil;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                           AppUserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Bu kullanıcı adı zaten alınmış.");
        }
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Bu email zaten kayıtlı.");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_USER bulunamadı. Sistem seed verisi eksik olabilir."));

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
        return ResponseEntity.ok("Kayıt başarılı.");
      }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDto(token));
    }
}

