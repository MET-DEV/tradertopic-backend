package com.tradertopic.metsoft.controller;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SlowController {

    private final Tracer tracer;

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

        return Map.of(
                "requestedDelayMs", ms,
                "status", "completed"
        );
    }
}

