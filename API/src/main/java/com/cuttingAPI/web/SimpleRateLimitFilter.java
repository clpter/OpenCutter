package com.cuttingAPI.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleRateLimitFilter extends OncePerRequestFilter {

    @Value("${api.rate-limit.max-requests-per-minute:30}")
    private int maxRequestsPerMinute;

    private final Map<String, RequestTracker> ipRequests = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Only rate limit API calculation endpoints
        if (request.getRequestURI().startsWith("/api/")) {
            String clientIp = getClientIp(request);
            long now = System.currentTimeMillis();

            // Periodic memory cleanup: prune old IPs if map grows large
            if (ipRequests.size() > 2000) {
                ipRequests.entrySet().removeIf(entry -> (now - entry.getValue().timestamp) > 60_000);
            }

            RequestTracker tracker = ipRequests.compute(clientIp, (key, current) -> {
                if (current == null || (now - current.timestamp) > 60_000) {
                    return new RequestTracker(now, 1);
                }
                current.count++;
                return current;
            });

            if (tracker.count > maxRequestsPerMinute) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("""
                    {"error": "Too Many Requests", "message": "Rate limit exceeded. Please wait a minute."}
                """);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        // Reads real IP from Azure / Docker / Proxy header if available
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RequestTracker {
        final long timestamp;
        int count;

        RequestTracker(long timestamp, int count) {
            this.timestamp = timestamp;
            this.count = count;
        }
    }
}
