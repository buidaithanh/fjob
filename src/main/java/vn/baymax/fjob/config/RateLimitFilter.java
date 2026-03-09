package vn.baymax.fjob.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String endpoint = request.getRequestURI();
        String clientIdentifier = getClientIdentifier(request);

        String key = clientIdentifier + ":" + normalizeEndpoint(endpoint);

        Bucket bucket = resolveBucket(key, endpoint);

        if (bucket.tryConsume(1)) {
            long remaining = bucket.getAvailableTokens();
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setHeader("X-RateLimit-Retry-After", "60");
            response.getWriter().write(
                    "{\"error\":\"Too many requests\",\"message\":\"Rate limit exceeded for endpoint: "
                            + endpoint + "\",\"retryAfter\":60}");
        }
    }

    private Bucket resolveBucket(String key, String endpoint) {
        return cache.computeIfAbsent(key, k -> createBucketForEndpoint(endpoint));
    }

    /**
     * Tạo bucket với giới hạn khác nhau cho từng loại endpoint
     */
    private Bucket createBucketForEndpoint(String endpoint) {
        // 1. AUTH ENDPOINTS - Chống brute force
        if (endpoint.startsWith("/api/v1/auth/login") ||
                endpoint.startsWith("/api/v1/auth/register")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                    .build();
        }

        // 2. SEARCH/EXPENSIVE OPERATIONS
        if (endpoint.contains("/search") || endpoint.contains("/filter")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1))))
                    .build();
        }

        // 3. WRITE OPERATIONS (POST, PUT, DELETE)
        if (endpoint.startsWith("/api/v1/resumes") ||
                endpoint.startsWith("/api/v1/applications")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(20, Refill.intervally(20, Duration.ofMinutes(1))))
                    .build();
        }

        // 4. READ OPERATIONS - Có thể generous hơn
        if (endpoint.startsWith("/api/v1/jobs") ||
                endpoint.startsWith("/api/v1/companies") ||
                endpoint.startsWith("/api/v1/skills")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(1))))
                    .build();
        }

        // 5. DEFAULT
        return Bucket.builder()
                .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
                .build();
    }

    // Lấy identifier: ưu tiên User ID, fallback về IP
    private String getClientIdentifier(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal())) {
            return "user:" + auth.getName();
        }

        // Fallback về IP cho anonymous users
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return "ip:" + clientIp;
    }

    /**
     * Chuẩn hóa endpoint để group các path parameters
     * VD: /api/v1/jobs/123 và /api/v1/jobs/456 đều thành /api/v1/jobs/{id}
     */
    private String normalizeEndpoint(String endpoint) {
        if (endpoint.contains("?")) {
            endpoint = endpoint.substring(0, endpoint.indexOf("?"));
        }

        endpoint = endpoint.replaceAll("/\\d+(/|$)", "/{id}$1");

        return endpoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/storage/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}