package com.tradertopic.metsoft.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tradertopic.metsoft.entity.util.CachedBodyHttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> SENSITIVE_FIELDS = Arrays.asList(
            "password", "pwd", "token", "accessToken", "refreshToken",
            "cardNumber", "cvv", "cvc", "pin", "secret", "authorization"
    );

    private static final List<Pattern> SENSITIVE_PATTERNS = SENSITIVE_FIELDS.stream()
            .map(field -> Pattern.compile(
                    "(?i)(\"" + field + "\"\\s*:\\s*\")(.*?)(\")"
            ))
            .collect(Collectors.toList());

    private static final String MASK = "***MASKED***";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        logRequest(wrappedRequest);

        filterChain.doFilter(wrappedRequest, response);
    }

    private void logRequest(CachedBodyHttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = maskQueryString(request.getQueryString());
        String params = getParams(request);
        String body = maskSensitiveData(new String(request.getCachedBody(), StandardCharsets.UTF_8));

        log.info("Method: {}, URI: {}, QueryString: {}, Params: [{}], Body: {}",
                method, uri, queryString, params, body);
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String getParams(HttpServletRequest request) {
        Enumeration<String> paramNames = request.getParameterNames();
        if (!paramNames.hasMoreElements()) {
            return "";
        }
        return Collections.list(paramNames).stream()
                .map(name -> {
                    String value = isSensitive(name)
                            ? MASK
                            : String.join(",", request.getParameterValues(name));
                    return name + "=" + value;
                })
                .collect(Collectors.joining(", "));
    }


    private String maskSensitiveData(String body) {
        if (body == null || body.isEmpty()) {
            return body;
        }
        String result = body;
        for (Pattern pattern : SENSITIVE_PATTERNS) {
            Matcher matcher = pattern.matcher(result);
            result = matcher.replaceAll("$1" + MASK + "$3");
        }
        return result;
    }


    private String maskQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return queryString;
        }
        return Arrays.stream(queryString.split("&"))
                .map(pair -> {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2 && isSensitive(kv[0])) {
                        return kv[0] + "=" + MASK;
                    }
                    return pair;
                })
                .collect(Collectors.joining("&"));
    }

    private boolean isSensitive(String fieldName) {
        return SENSITIVE_FIELDS.stream().anyMatch(field -> field.equalsIgnoreCase(fieldName));
    }
}

