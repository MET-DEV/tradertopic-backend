package com.tradertopic.metsoft.entity.model.auth;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = Privilege.ENTITY_NAME)
public class Privilege {
	
	public static final String ENTITY_NAME = "Privilege";

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	} 
}

