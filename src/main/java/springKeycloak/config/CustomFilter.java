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
import springKeycloak.dto.ResponseDTO;
import springKeycloak.dto.RolePermissionsDTO;
import springKeycloak.dto.UserPermissionDTO;
import springKeycloak.models.UserRole;
import springKeycloak.repositories.RolePermissionRepo;
import springKeycloak.service.UserRoleService;
import springKeycloak.service.UserService;
import springKeycloak.utils.AppUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomFilter extends OncePerRequestFilter {


    private final UserService userService;
    private final RolePermissionRepo rolePermissionRepo;
    private final UserRoleService userRoleService;
    private final AppUtils appUtils;

    @Autowired
    public CustomFilter(UserService userService, RolePermissionRepo rolePermissionRepo, UserRoleService userRoleService, AppUtils appUtils) {
        this.userService = userService;
        this.rolePermissionRepo = rolePermissionRepo;
        this.userRoleService = userRoleService;
        this.appUtils = appUtils;
    }

    /**
     * This is used to perform necessary checks and set the required attributes and values after authentication.
     * This code runs after the JWTConverter but before the controller methods.
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Collection<GrantedAuthority> authorities = new ArrayDeque<>();
        String username = "";
        // getting already authenticated user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // loading user permissions from the db the username
        if (authentication != null){
            username = authentication.getName();
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
        }
        // iterating and setting each role permission in the granted authority collection
        // the purpose of this is to extract user role permissions and add it to the user specific permissions
        if (authentication != null){
            ResponseDTO role = userRoleService.getUserRoleByUserId(appUtils.getAuthenticatedUserId());
            System.out.println("ROLE:=======:" + role.getData());
            System.out.println("USER ID:======:" + appUtils.getAuthenticatedUserId());
            if (role != null && role.getData() != null){
                List<RolePermissionsDTO> rolePermissionsDTOList = rolePermissionRepo.findRolePermissionsByRoleName(role.getData().toString());
                if (rolePermissionsDTOList !=null){
                    for (RolePermissionsDTO rolePermission:rolePermissionsDTOList){
                        authorities.add(new SimpleGrantedAuthority(rolePermission.getPermission()));
                    }
                }
            }
            // removing duplicates
            Set<GrantedAuthority> authoritySet = authorities.stream().collect(Collectors.toSet());
            // adding existing user permissions to the permissions loaded from the db
            authoritySet.addAll(authentication.getAuthorities());
            // setting them back to the authentication object
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username,null, authoritySet);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
            filterChain.doFilter(request, response);
        }

        filterChain.doFilter(request, response);

    }
}
