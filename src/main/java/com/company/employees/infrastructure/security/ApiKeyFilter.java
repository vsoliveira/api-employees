package com.company.employees.infrastructure.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * API Key Authentication Filter.
 * Validates X-API-Key header on business API requests.
 */
@Component
public class ApiKeyFilter implements Filter {
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String[] EXCLUDED_PATH_PATTERNS = {
        "/**/swagger-ui.html",
        "/**/swagger-ui/**",
        "/**/v1/api-docs",
        "/**/v1/api-docs/**",
        "/**/v3/api-docs",
        "/**/v3/api-docs/**",
        "/**/actuator",
        "/**/actuator/**"
    };

    @Value("${api.key:default-api-key}")
    private String expectedApiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = httpRequest.getRequestURI();

        if (isExcludedPath(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        String providedApiKey = httpRequest.getHeader(API_KEY_HEADER);

        if (providedApiKey == null || !providedApiKey.equals(expectedApiKey)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Invalid or missing API key\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    @SuppressWarnings("null")
    private boolean isExcludedPath(String requestPath) {
        for (String excludedPathPattern : EXCLUDED_PATH_PATTERNS) {
            if (PATH_MATCHER.match(excludedPathPattern, requestPath)) {
                return true;
            }
        }
        return false;
    }
}

