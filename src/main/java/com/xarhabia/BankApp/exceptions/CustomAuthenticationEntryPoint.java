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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.warn("Intento de acceso no autenticado a {} desde IP {}", request.getRequestURI(), request.getRemoteAddr());

        GeneralResponse body = new GeneralResponse(
                "401",
                "No autenticado. Verifique sus credenciales o el token de acceso",
                false,
                null
        );

        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
