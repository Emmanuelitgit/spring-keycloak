package springKeycloak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springKeycloak.models.UserRole;
import springKeycloak.service.UserRoleService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserRoleRest {

    private final UserRoleService userRoleService;

    @Autowired
    public UserRoleRest(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/{userId}/{roleId}/assign-role")
    public ResponseEntity<Object> saveUserRole(@PathVariable UUID roleId, @PathVariable UUID userId){
        UserRole userRole = userRoleService.saveUserRole(roleId, userId);
        return new ResponseEntity<>(userRole, HttpStatus.OK);
    }
}
