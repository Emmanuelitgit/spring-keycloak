package springKeycloak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.RolePermission;
import springKeycloak.service.RolePermissionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RolePermissionRest {

    private final RolePermissionService rolePermissionService;

    @Autowired
    public RolePermissionRest(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @PostMapping("/{roleId}")
    public ResponseEntity<ResponseDTO> saveRolePermission(@RequestBody List<UUID> permissions, @PathVariable UUID roleId){
        return rolePermissionService.saveRolePermission(permissions, roleId);
    }
}
