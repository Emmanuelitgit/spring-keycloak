package springKeycloak.controllers;

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
    public ResponseEntity<Object> getUsers(){
        List<User> users = userService.getUsers();
        return new ResponseEntity<>(users, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID id){
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatusCode.valueOf(200));
    }

    @PostMapping("/{userId}/assign/permissions")
    public ResponseEntity<Object> saveUserPermissions(@PathVariable UUID userId, @RequestBody List<UUID> permissionIds){
        List<UserPermission> userPermission = userService.saveUserPermissions(userId, permissionIds);
        return new ResponseEntity<>(userPermission, HttpStatusCode.valueOf(200));
    }

    @GetMapping("/{userId}/permissions")
    public ResponseEntity<Object> getUserPermissionsByUserId(@PathVariable UUID userId){
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        List<UserPermissionDTO> permissions = userService.getUserPermissionsByUserId(userId);
        return new ResponseEntity<>(permissions, HttpStatusCode.valueOf(200));
    }
}
