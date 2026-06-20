package com.tradertopic.metsoft.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tradertopic.metsoft.entity.dto.SaveUserDto;
import com.tradertopic.metsoft.entity.event.UserCreatedEvent;
import com.tradertopic.metsoft.kafka.producer.UserEventProducer;


@RestController
@RequestMapping("/users")
public class UserController {
	
	private final UserEventProducer producer;
	
	public UserController(UserEventProducer producer) {
        this.producer = producer;
    }

	
	@PostMapping
	public ResponseEntity<String> saveUser(@RequestBody SaveUserDto saveUserDto){    
	    UserCreatedEvent userCreatedEvent = new UserCreatedEvent();
	    userCreatedEvent.setMail(saveUserDto.getMail());
	    userCreatedEvent.setUsername(saveUserDto.getUserName());
	    producer.sendUserCreatedEvent(userCreatedEvent);
	    return ResponseEntity.ok("Kullanici Aktifleştirme Maili, Mail Adresinize Gönderilecektir.");
}

}
