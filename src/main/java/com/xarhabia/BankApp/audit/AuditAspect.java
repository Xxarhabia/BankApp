package com.xarhabia.BankApp.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String username = resolveUsername();
        String action = auditable.action();

        try {
            Object result = joinPoint.proceed();

            auditLogger.info(
                    "action={} user={} result=SUCCESS",
                    action,
                    username
            );
            return result;
        } catch (Exception e) {
            auditLogger.warn(
                    "action={} user={} result=FAILURE reason={}",
                    action,
                    username,
                    e.getMessage()
            );
            throw e;
        }
    }

    private String resolveUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())){
            return "ANONYMOUS";
        }
        return auth.getName();
    }
}
