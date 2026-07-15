package com.turing.saasmanager.security;

import java.io.IOException;
import java.time.LocalDateTime;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String json = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"Unauthorized\", \"mensaje\": \"Acceso no autorizado: debes proporcionar un token JWT válido en el encabezado Authorization (Bearer <token>)\", \"path\": \"%s\"}",
                LocalDateTime.now().toString(),
                HttpServletResponse.SC_UNAUTHORIZED,
                request.getServletPath()
        );

        response.getWriter().write(json);
    }
}
