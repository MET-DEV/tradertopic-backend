package com.tradertopic.metsoft.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tradertopic.metsoft.entity.event.UserCreatedEvent;

@Service
public class UserEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UserEventConsumer.class);

    @KafkaListener(topics = "user-created", groupId = "tradertopic-group")
    public void consume(UserCreatedEvent event) {
        log.info("UserCreatedEvent alındı -> username: {}, mail: {}",
                event.getUsername(), event.getMail());
    }
}

