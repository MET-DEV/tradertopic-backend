package com.tradertopic.metsoft.services.auth;
import com.tradertopic.metsoft.entity.model.auth.AppUser;
import com.tradertopic.metsoft.entity.model.auth.RefreshToken;
import com.tradertopic.metsoft.repository.RefreshTokenRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${app.jwt.refresh-expiration-ms:604800000}") 
    private long refreshExpirationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(AppUser user) {
        refreshTokenRepository.deleteByUser_Id(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz refresh token."));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token iptal edilmiş.");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token süresi dolmuş, yeniden giriş yapın.");
        }

        return refreshToken;
    }

    public void revokeByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
            log.info("Refresh token iptal edildi, userId={}", rt.getUser().getId());
        });
    }
}