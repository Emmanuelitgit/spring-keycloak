package springKeycloak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.setup.PermissionCategorySetup;
import springKeycloak.service.PermissionCategorySetupService;

@RestController
@RequestMapping("/api/permission-setups-categories")
public class PermissionCategorySetupRest {

    private final PermissionCategorySetupService permissionCategorySetupService;

    @Autowired
    public PermissionCategorySetupRest(PermissionCategorySetupService permissionCategorySetupService) {
        this.permissionCategorySetupService = permissionCategorySetupService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAll(){
        return permissionCategorySetupService.getAll();
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> save(@RequestBody PermissionCategorySetup payload){
        return permissionCategorySetupService.save(payload);
    }
}
