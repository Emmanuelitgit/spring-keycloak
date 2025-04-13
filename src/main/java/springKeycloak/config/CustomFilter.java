package springKeycloak.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import springKeycloak.dto.UserPermissionDTO;
import springKeycloak.service.UserService;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
public class CustomFilter extends OncePerRequestFilter {


    private final UserService userService;

    @Autowired
    public CustomFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);

        Collection<GrantedAuthority> authorities = new ArrayDeque<>();
        // getting already authenticated user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // loading user permissions from the db the username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserPermissionDTO> permissions = userService.getUserPermissionsByUsername(username);
        // iterating and setting each permission in the granted authority collection
        if (permissions == null){
            filterChain.doFilter(request, response);
        }
        if(permissions != null){
            for (UserPermissionDTO permission:permissions){
                authorities.add(new SimpleGrantedAuthority(permission.getPermission()));
            }
        }
        // adding permissions loaded from th db to the existing authorities
        authorities.addAll(authentication.getAuthorities());
        // setting them back to the authentication object
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username,null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
