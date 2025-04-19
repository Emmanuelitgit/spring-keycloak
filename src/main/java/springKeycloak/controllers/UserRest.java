package springKeycloak.controllers;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springKeycloak.Exception.NotFoundException;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.dto.UserDTO;
import springKeycloak.dto.UserPermissionDTO;
import springKeycloak.models.User;
import springKeycloak.models.UserPermission;
import springKeycloak.service.KeyCloakService;
import springKeycloak.service.UserService;
import springKeycloak.utils.AppUtils;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserRest {

    private final UserService userService;

    @Autowired
    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> saveUser(@RequestBody UserDTO userPayload){
        System.out.println("Data:====" + userPayload);
        ResponseDTO user = userService.saveUser(userPayload);
        if (user == null){
            ResponseDTO response = AppUtils.getResponseDto("", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(400));
        }
        ResponseDTO response = AppUtils.getResponseDto("user record saved",HttpStatus.CREATED, user);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(201));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID id){
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatusCode.valueOf(200));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable UUID userId,@RequestBody UserDTO payload){
        return userService.updateUser(userId, payload);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable UUID userId){
        userService.deleteUser(userId);
        ResponseDTO response = AppUtils.getResponseDto("user deleted successfully", HttpStatus.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/permissions")
    public ResponseEntity<Object> getUserPermissionsByUserId(@PathVariable UUID userId){
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        List<UserPermissionDTO> permissions = userService.getUserPermissionsByUserId(userId);
        return new ResponseEntity<>(permissions, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/user-permissions-and-role-permissions")
    public ResponseEntity<ResponseDTO> getUserPermissionsAndRolePermissions(){
        System.out.println("HELLO");
        return userService.getUserPermissionsAndRolePermissions();
    }

    @GetMapping("/keycloak-users")
    public ResponseEntity<ResponseDTO> getKeycloakUsers(){
        List<UserRepresentation> userRepresentations = KeyCloakService.getKeycloakUsers();
        ResponseDTO response = AppUtils.getResponseDto("keycloak users", HttpStatus.OK, userRepresentations);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
