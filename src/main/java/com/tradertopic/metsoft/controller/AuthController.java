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
import com.tradertopic.metsoft.entity.dto.RefreshTokenRequestDto;
import com.tradertopic.metsoft.entity.dto.RegisterRequestDto;
import com.tradertopic.metsoft.entity.model.auth.AppUser;
import com.tradertopic.metsoft.entity.model.auth.RefreshToken;
import com.tradertopic.metsoft.entity.model.auth.Role;
import com.tradertopic.metsoft.repository.AppUserRepository;
import com.tradertopic.metsoft.repository.RoleRepository;
import com.tradertopic.metsoft.services.auth.AppUserDetailsService;
import com.tradertopic.metsoft.services.auth.JwtUtil;
import com.tradertopic.metsoft.services.auth.RefreshTokenService;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthenticationManager authenticationManager;
	private final AppUserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final RefreshTokenService refreshTokenService;
	private final AppUserDetailsService userDetailsService;

	public AuthController(AuthenticationManager authenticationManager, AppUserRepository userRepository,
			RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
			RefreshTokenService refreshTokenService, AppUserDetailsService userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
		this.refreshTokenService = refreshTokenService;
		this.userDetailsService = userDetailsService;
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
				.orElseThrow(() -> new IllegalStateException("ROLE_USER bulunamadı."));
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
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String accessToken = jwtUtil.generateToken(userDetails);
		AppUser user = userRepository.findByUsername(request.username())
				.orElseThrow(() -> new IllegalStateException("Kullanıcı bulunamadı."));
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
		return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken.getToken()));
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenRequestDto request) {
		RefreshToken storedToken = refreshTokenService.verifyAndGet(request.refreshToken());
		AppUser user = storedToken.getUser();
		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
		String newAccessToken = jwtUtil.generateToken(userDetails);
		RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
		return ResponseEntity.ok(new AuthResponseDto(newAccessToken, newRefreshToken.getToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshTokenRequestDto request) {
		refreshTokenService.revokeByToken(request.refreshToken());
		return ResponseEntity.ok("Çıkış yapıldı.");
	}
}
