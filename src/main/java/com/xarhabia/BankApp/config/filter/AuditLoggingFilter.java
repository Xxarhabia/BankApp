package com.xarhabia.BankApp.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


public class AuditLoggingFilter extends OncePerRequestFilter {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logAudit(request, response, duration);
            MDC.clear();
        }
    }

    private void logAudit(HttpServletRequest req, HttpServletResponse resp, long durationMs) {
        String username = resolveUsername();
        String ip = resolveClientIp(req);

        auditLogger.info(
                "method={} uri={} status={} user={} ip={} duration={}",
                req.getMethod(),
                req.getRequestURI(),
                resp.getStatus(),
                username,
                ip,
                durationMs
        );
    }

    private String resolveUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "ANONYMOUS";
        }
        return auth.getName();
    }

    private String resolveClientIp(HttpServletRequest req) {
        String xfHeader = req.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return req.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
