package springKeycloak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.setup.PermissionSetUp;
import springKeycloak.repositories.PermissionSetUpRepo;
import springKeycloak.service.PermissionSetUpService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permission-setups")
public class PermissionSetUpRest {

    private final PermissionSetUpService permissionSetUpService;
    private final PermissionSetUpRepo permissionSetUpRepo;

    @Autowired
    public PermissionSetUpRest(PermissionSetUpService permissionSetUpService, PermissionSetUpRepo permissionSetUpRepo) {
        this.permissionSetUpService = permissionSetUpService;
        this.permissionSetUpRepo = permissionSetUpRepo;
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

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable UUID id, @RequestBody PermissionSetUp permissionSetUp){
        System.out.println("IN CONTROLLER:====" + permissionSetUp.getName());
        return permissionSetUpService.updatePermissionSetup(permissionSetUp, id);
    }

    @GetMapping("/permissions-and-categories")
    public ResponseEntity<ResponseDTO> getPermissionSetUpsAndCategory(){
        return permissionSetUpService.getPermissionSetUpsAndCategory();
    }
 }
