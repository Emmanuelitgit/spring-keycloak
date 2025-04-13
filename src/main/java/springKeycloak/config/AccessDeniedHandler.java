package springKeycloak.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> responseData = new HashMap<>();
        responseData.put("message", "You do not have permission to access this resource.");
        responseData.put("status", "403");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(responseData));
        response.setStatus(403);
    }
}
