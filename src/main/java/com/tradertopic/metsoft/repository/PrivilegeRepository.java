package com.tradertopic.metsoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradertopic.metsoft.entity.model.auth.Privilege;

import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByName(String name);
}
