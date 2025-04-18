package springKeycloak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.RoleSetUp;
import springKeycloak.service.RoleSetUpService;

import java.util.List;

@RestController
@RequestMapping("/api/role-setups")
public class RoleSetUpRest {

    private final RoleSetUpService roleSetUpService;

    @Autowired
    public RoleSetUpRest(RoleSetUpService roleSetUpService) {
        this.roleSetUpService = roleSetUpService;
    }

    @PostMapping
    public ResponseEntity<Object> saveRole(@RequestBody RoleSetUp roleSetUpPayload){
        RoleSetUp roleSetUp = roleSetUpService.saveRole(roleSetUpPayload);
        return new ResponseEntity<>(roleSetUp, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getAllRoles(){
        return roleSetUpService.getAllRoles();
    }
}
