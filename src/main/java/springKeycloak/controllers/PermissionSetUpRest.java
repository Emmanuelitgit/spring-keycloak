package springKeycloak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.setup.PermissionSetUp;
import springKeycloak.service.PermissionSetUpService;

import java.util.List;

@RestController
@RequestMapping("/api/permission-setups")
public class PermissionSetUpRest {

    private final PermissionSetUpService permissionSetUpService;

    @Autowired
    public PermissionSetUpRest(PermissionSetUpService permissionSetUpService) {
        this.permissionSetUpService = permissionSetUpService;
    }

    @PostMapping
    public ResponseEntity<PermissionSetUp> savePermissionSetup(@RequestBody PermissionSetUp permissionSetUp){
        PermissionSetUp permissionSetUpData = permissionSetUpService.savePermissionSetup(permissionSetUp);
        return new ResponseEntity<>(permissionSetUpData, HttpStatusCode.valueOf(201));
    }

    @GetMapping
    public ResponseEntity<Object> getPermissionSetups(){
        List<PermissionSetUp> permissionSetUps = permissionSetUpService.getPermissionSetups();
        return new ResponseEntity<>(permissionSetUps, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/permission-setups/role-default-permissions/{role}")
    public ResponseEntity<ResponseDTO> getPermissionSetupsAndRoleDefaultPermissions(@PathVariable String role){
        return permissionSetUpService.getPermissionSetupsAndRoleDefaultPermissions(role.toUpperCase());
    }
 }
