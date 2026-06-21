package com.tradertopic.metsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradertopic.metsoft.entity.model.auth.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
