package ru.nikidzawa.responses.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        new ObjectMapper().writeValue(
                response.getWriter(),
                Exception.builder()
                        .code(HttpStatus.UNAUTHORIZED.value())
                        .message("Доступ запрещен: Недостаточно прав").build()
        );
    }
}
