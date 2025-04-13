package springKeycloak.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import springKeycloak.dto.UserPermissionDTO;
import springKeycloak.models.User;
import springKeycloak.models.UserPermission;
import springKeycloak.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Object> saveUser(@RequestBody User userPayload){
        System.out.println("Data:====" + userPayload);
        User user = userService.saveUser(userPayload);
        if (user == null){
            return new ResponseEntity<>("An error occur in saving user record", HttpStatusCode.valueOf(400));
        }
        return new ResponseEntity<>(user, HttpStatusCode.valueOf(201));
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(){
        List<User> users = userService.getUsers();
        if (users.isEmpty()){
            return new ResponseEntity<>("No user record found", HttpStatusCode.valueOf(404));
        }
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
