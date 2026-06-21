package com.tradertopic.metsoft.entity.model.auth;

import com.tradertopic.metsoft.controller.SlowController;
import com.tradertopic.metsoft.repository.AppUserRepository;
import com.tradertopic.metsoft.repository.PrivilegeRepository;
import com.tradertopic.metsoft.repository.RoleRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.email:admin@tradertopic.com}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;
    
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    @org.springframework.core.annotation.Order(1)
    public CommandLineRunner seedRolesAndPrivileges(RoleRepository roleRepo,
                                                      PrivilegeRepository privilegeRepo) {
        return args -> {
            if (roleRepo.count() > 0) return;

            Privilege userRead = newPrivilege(privilegeRepo, "USER_READ");
            Privilege userWrite = newPrivilege(privilegeRepo, "USER_WRITE");
            Privilege orderApprove = newPrivilege(privilegeRepo, "ORDER_APPROVE");

            Role admin = new Role();
            admin.setName("ROLE_ADMIN");
            admin.setPrivileges(Set.of(userRead, userWrite, orderApprove));
            roleRepo.save(admin);

            Role user = new Role();
            user.setName("ROLE_USER");
            user.setPrivileges(Set.of(userRead));
            roleRepo.save(user);
        };
    }

    @Bean
    @org.springframework.core.annotation.Order(2)
    public CommandLineRunner seedAdminUser(AppUserRepository userRepo,
                                            RoleRepository roleRepo,
                                            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepo.existsByUsername(adminUsername)) {
                return; 
            }

            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new IllegalStateException(
                            "ROLE_ADMIN bulunamadı. seedRolesAndPrivileges önce çalışmalı."));

            AppUser admin = new AppUser();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(Set.of(adminRole));

            userRepo.save(admin);
            log.info("Admin kullanıcı oluşturuldu={}", adminUsername);
        };
    }

    private Privilege newPrivilege(PrivilegeRepository repo, String name) {
        Privilege p = new Privilege();
        p.setName(name);
        return repo.save(p);
    }
}
