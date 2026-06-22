package com.xarhabia.BankApp.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xarhabia.BankApp.utils.dto.response.GeneralResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.warn("Intento de acceso no permidido a {} desde IP {}", request.getRequestURI(), request.getRemoteAddr());

        GeneralResponse body = new GeneralResponse(
                "403",
                "No tiene permisos para acceder a este recurso",
                false,
                null
        );

        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
