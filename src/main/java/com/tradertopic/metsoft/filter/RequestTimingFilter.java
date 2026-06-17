package com.tradertopic.metsoft.filter;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestTimingFilter extends HttpFilter {

	private static final long serialVersionUID = 2071782474377678090L;

	private static final Logger log = LoggerFactory.getLogger(RequestTimingFilter.class);

    private final Tracer tracer;

    @Autowired
    public RequestTimingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;

            var currentSpan = tracer.currentSpan();
            if (currentSpan != null) {
                currentSpan.tag("request.duration_ms", String.valueOf(durationMs));
                currentSpan.tag("http.target", request.getRequestURI());
            }

            log.info("{} {} -> {} ({} ms)",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs);
        }
    }
}
