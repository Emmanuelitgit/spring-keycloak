package springKeycloak.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.dto.RolePermissionsDTO;
import springKeycloak.repositories.RolePermissionRepo;
import springKeycloak.repositories.UserPermissionRepo;
import springKeycloak.service.UserService;
import springKeycloak.utils.AppUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomFilter extends OncePerRequestFilter {


    private final UserService userService;
    private final RolePermissionRepo rolePermissionRepo;
    private final AppUtils appUtils;
    private final UserPermissionRepo userPermissionRepo;

    @Autowired
    public CustomFilter(UserService userService, RolePermissionRepo rolePermissionRepo, AppUtils appUtils, UserPermissionRepo userPermissionRepo) {
        this.userService = userService;
        this.rolePermissionRepo = rolePermissionRepo;
        this.appUtils = appUtils;
        this.userPermissionRepo = userPermissionRepo;
    }

    /**
     * This is used to perform necessary checks and set the required attributes and values after authentication.
     * This code runs after the JWTConverter but before the controller methods.
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if(request.getHeader("Authorization") != null){
//            String token = request.getHeader("Authorization").substring(7);
//            List<KeycloakPermissionsDTO> keycloakPermissions = appUtils.getUserPermissionsFromKeycloak(token);
//
//            for (KeycloakPermissionsDTO keycloakPermission : keycloakPermissions){
//                System.out.println(keycloakPermission.getScopes());
//            }
//
//        }
        Collection<GrantedAuthority> authorities = new ArrayDeque<>();
        // getting already authenticated user details
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // iterating and setting each role permission in the granted authority collection
        // the purpose of this is to extract user role permissions and add it to the user specific permissions
        if (authentication != null){
            UUID userId = appUtils.getAuthenticatedUserId();
            ResponseDTO role = userService.getUserRoleByUserId(appUtils.getAuthenticatedUserId());
            if (role.getData() != null){
                List<RolePermissionsDTO > permissions = userService.getPermissions(userId, role.getData().toString());
                if (!permissions.isEmpty()){
                    for (RolePermissionsDTO permission:permissions){
                        authorities.add(new SimpleGrantedAuthority(permission.getPermission()));
                    }
                }
            }
            System.out.println("ROLE:=======:" + role.getData());
            System.out.println("USER ID:======:" + appUtils.getAuthenticatedUserId());

            // adding existing user permissions to the permissions loaded from the db
            authorities.addAll(authentication.getAuthorities());
            // removing duplicates
            Set<GrantedAuthority> authoritiesSet = authorities.stream().collect(Collectors.toSet());
            // setting them back to the authentication object
            String username = AppUtils.getAuthenticatedUsername();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username,null, authoritiesSet);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
        }

        filterChain.doFilter(request, response);

    }
}
