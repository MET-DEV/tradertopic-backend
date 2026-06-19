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
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

@Component
public class RequestTimingFilter extends HttpFilter {

	private static final long serialVersionUID = 2071782474377678090L;

	private static final Logger log = LoggerFactory.getLogger(RequestTimingFilter.class);
	
	 private static final List<String> EXCLUDED_PATHS = List.of(
	            "/actuator/**",
	            "/swagger-ui/**",
	            "/v3/api-docs/**"
	    );
	 private final AntPathMatcher pathMatcher = new AntPathMatcher();


    private final Tracer tracer;

    @Autowired
    public RequestTimingFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

    	if (isExcluded(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
    	
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
    
    private boolean isExcluded(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

 
}
