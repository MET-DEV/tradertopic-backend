package com.tradertopic.metsoft.controller;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tradertopic.metsoft.entity.dto.StockSaveDto;

import java.util.Map;

@RestController
public class SlowController {

    private final Tracer tracer;
    private static final Logger log = LoggerFactory.getLogger(SlowController.class);

    @Autowired
    public SlowController(Tracer tracer) {
        this.tracer = tracer;
    }

    @GetMapping("/api/slow")
    public Map<String, Object> slow(@RequestParam(name = "ms", defaultValue = "1000") long ms) throws InterruptedException {
        Span span = tracer.nextSpan().name("simulated-work");
        span.start();
        try {
            span.tag("simulated.delay_ms", String.valueOf(ms));
            Thread.sleep(ms);
        } finally {
            span.end();
        }
        log.warn("İşlem çok uzun sürdü={} ms", ms);
        return Map.of(
                "requestedDelayMs", ms,
                "status", "completed"
        );
    }
    
    @PostMapping("/api/stockSave")
    public ResponseEntity<String> saveStock(@RequestBody StockSaveDto stockSaveDto){
    	return ResponseEntity.ok("Test");
    }
}

