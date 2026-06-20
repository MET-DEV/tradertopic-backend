package com.tradertopic.metsoft.kafka.producer;

import com.tradertopic.metsoft.entity.event.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {

    private static final Logger log = LoggerFactory.getLogger(UserEventProducer.class);
    private static final String TOPIC = "user-created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.getUsername(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("UserCreatedEvent gönderildi: {} -> partition {}, offset {}",
                                event.getUsername(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("UserCreatedEvent gönderilemedi: {}", event.getUsername(), ex);
                    }
                });
    }
}

