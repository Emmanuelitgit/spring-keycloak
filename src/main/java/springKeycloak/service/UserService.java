package springKeycloak.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import springKeycloak.Exception.InvalidDataException;
import springKeycloak.Exception.NotFoundException;
import springKeycloak.dto.*;
import springKeycloak.models.*;
import springKeycloak.repositories.PermissionSetUpRepo;
import springKeycloak.repositories.UserPermissionRepo;
import springKeycloak.repositories.UserRepository;
import springKeycloak.repositories.UserRoleRepo;
import springKeycloak.utils.AppUtils;

import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PermissionSetUpRepo permissionSetUpRepo;
    private final UserPermissionRepo userPermissionRepo;
    private final AppUtils appUtils;
    private final UserRoleRepo userRoleRepo;
    private final PermissionSetUpService permissionSetUpService;
    private final RoleSetUpService roleSetUpService;
    private final KeyCloakService keyCloakService;

    @Autowired
    public UserService(UserRepository userRepository, PermissionSetUpRepo permissionSetUpRepo, UserPermissionRepo userPermissionRepo, AppUtils appUtils, UserRoleRepo userRoleRepo, PermissionSetUpService permissionSetUpService, RoleSetUpService roleSetUpService, KeyCloakService keyCloakService) {
        this.userRepository = userRepository;
        this.permissionSetUpRepo = permissionSetUpRepo;
        this.userPermissionRepo = userPermissionRepo;
        this.appUtils = appUtils;
        this.userRoleRepo = userRoleRepo;
        this.permissionSetUpService = permissionSetUpService;
        this.roleSetUpService = roleSetUpService;
        this.keyCloakService = keyCloakService;
    }

    /**
     * This method is used to fetch all users from the db.
     * @return list of users
     * @auther Emmanuel Yidana
     * @createdAt 18h April 2025
     */
    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','VIEW_USERS')")
    public ResponseEntity<ResponseDTO> getUsers(){
        List<User> users = userRepository.findAll();
        ResponseDTO response = AppUtils.getResponseDto("users fetched successfully", HttpStatus.OK, users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method is used to save new user to the database.
     * @param userPayload
     * @return userDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR', 'CREATE_USER')")
    @Transactional
    public ResponseDTO saveUser(UserDTO userPayload){
        if (userPayload == null){
            throw new InvalidDataException("Invalid data");
        }
        User user = new User();
        user.setEmail(userPayload.getEmail());
        user.setFirstName(userPayload.getFirstName());
        user.setUsername(userPayload.getUsername());
        user.setPassword(userPayload.getPassword());
        user.setLastName(userPayload.getLastName());
        User userData = userRepository.save(user);

        if (userData.getId() == null){
           throw new NotFoundException("permission record not found");
        }
        saveUserPermissions(userData.getId(), userPayload.getPermissions());
        saveUserRole(userPayload.getRole(), userData.getId());
        // saving user to keycloak
        keyCloakService.addUserToKeycloak(userPayload);
        return AppUtils.getResponseDto("record added successfully", HttpStatus.CREATED,userPayload);
    }

    /**
     * This method is used to get user records by the user id.
     * @param id
     * @return userDto
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public User getUserById(UUID id){
        boolean hasAuthority = AppUtils.getAuthenticatedUserAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("VIEW_USER_LIST") || auth.equals("SYSTEM ADMINISTRATOR"));
        if (appUtils.getAuthenticatedUserId().equals(id) || hasAuthority){
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()){
                throw new NotFoundException("user record not found");
            }
            return userOptional.get();
        }else {
            throw new AccessDeniedException("Unauthorized");
        }

    }

    /**
     * This method is used to update user records given the user id.
     * @param userId
     * @param payload
     * @return userDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','UPDATE_USER')")
    @Transactional
    public ResponseEntity<ResponseDTO> updateUser(UUID userId, UserDTO payload){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()){
            ResponseDTO response = AppUtils.getResponseDto("user record not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        if (payload != null){
            User user = userOptional.get();
            user.setEmail(payload.getEmail());
            user.setFirstName(payload.getFirstName());
            user.setLastName(payload.getLastName());
            user.setUsername(payload.getUsername());
            userRepository.save(user);
            saveUserPermissions(userId, payload.getPermissions());
            saveUserRole(payload.getRole(), userId);
            ResponseDTO response = AppUtils.getResponseDto("user saved successfully", HttpStatus.OK, payload);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        ResponseDTO response = AppUtils.getResponseDto("invalid user payload", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method is used to permanently remove user given the user id.
     * @param userId
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @Transactional
    public void deleteUser(UUID userId){
        userRepository.deleteById(userId);
        removeUserPermissions(userId);
        removeUserRole(userId);
    }

    /**
     * This method is used to change the status of user given the user id.
     * @param userId
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public boolean toggleUserStatus(UUID userId){
        return false;
    }
    /**
     * This method is used to save user permissions. call when saving or updating user records.
     * @param userId
     * @param permissions
     * @return userDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @Transactional
    public void saveUserPermissions(UUID userId, List<UUID> permissions){
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NullPointerException("User record not found");
        }
       
        if (!permissions.isEmpty()){
            removeUserPermissions(userId);
            for (UUID id:permissions){
                UserPermission userPermission = new UserPermission();
                Optional<PermissionSetUp> permissionSetUpOptional = permissionSetUpRepo.findById(id);
                if (permissionSetUpOptional.isPresent()){
                    userPermission.setUserId(userId);
                    userPermission.setPermissionId(id);
                    userPermission.setCreatedAt(ZonedDateTime.now());
                    userPermission.setCreatedBy(appUtils.getAuthenticatedUserId());
                    userPermissionRepo.save(userPermission);
                }
            }
        }
    }

    /**
     * This method is used to save user role. call when saving or updating user records.
     * @param userId
     * @param roleId
     * @return userDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @Transactional
    public void saveUserRole(UUID roleId, UUID userId){
        User user = getUserById(userId);
        RoleSetUp roleSetUp = roleSetUpService.getRoleById(roleId);

        if (user !=null && roleSetUp != null){
            removeUserRole(userId);
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreatedAt(ZonedDateTime.now());
            userRole.setCreatedBy(appUtils.getAuthenticatedUserId());
            userRoleRepo.save(userRole);
        }else {
            throw new NullPointerException("rol or user record not found");
        }
    }

    /**
     * This method is used to remove user permissions given the user id.
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public void removeUserPermissions(UUID userId){
        userPermissionRepo.deleteAllByUserId(userId);
    }

    /**
     * This method is used to remove user role given the user id.
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public void removeUserRole(UUID userId){
        userRoleRepo.deleteUserRoleByUserId(userId);
    }

    /**
     * This method is used to get user role by the user id.
     * @param id
     * @return userRoleDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @Cacheable(value = "userRole", key = "#id")
    public ResponseDTO getUserRoleByUserId(UUID id){
        System.out.println("Calling DB for user role...");
        Optional<UserRoleDTO> userRoleOptional = userRoleRepo.findUserRoleByUserId(id);
        if (userRoleOptional.isEmpty()){
            return AppUtils.getResponseDto("no role record found", HttpStatus.NOT_FOUND);
        }
        return AppUtils.getResponseDto("user role", HttpStatus.OK, userRoleOptional.get().getRole());
    }

    /**
     * This method is used to get permissions by the user id.
     * @param userId
     * @return userPermissionDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public List<UserPermissionDTO> getUserPermissionsByUserId(UUID userId){
        boolean hasAuthority = AppUtils.getAuthenticatedUserAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("MANAGE_PERMISSIONS") || auth.equals("SYSTEM ADMINISTRATOR"));
      if (appUtils.getAuthenticatedUserId().equals(userId) || hasAuthority){
          List<UserPermissionDTO> userPermissionOptional = userPermissionRepo.getUserPermissionsByUserId(userId);
          if (userPermissionOptional.isEmpty()){
              throw new NullPointerException("No user permission record found");
          }
          return userPermissionOptional.stream().toList();
      }
      else {
          throw new AccessDeniedException("Unauthorized");
      }
    }

    /**
     * This method is used to permissions by the user username.
     * @param username
     * @return userPermissionDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public List<UserPermissionDTO> getUserPermissionsByUsername(String username){
        boolean hasAuthority = AppUtils.getAuthenticatedUserAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("MANAGE_PERMISSIONS") || auth.equals("SYSTEM ADMINISTRATOR"));
        String authenticatedUsername = AppUtils.getAuthenticatedUsername();
        if (Objects.equals(authenticatedUsername, username) || hasAuthority){
            List<UserPermissionDTO> userPermissionOptional = userPermissionRepo.getUserPermissionsByUsername(username);
            if (userPermissionOptional.isEmpty()){
                return null;
            }
            return userPermissionOptional.stream().toList();
        }
        else {
            throw new AccessDeniedException("Unauthorized");
            }
    }

    /**
     * This method is used to get user permissions and the user role permissions.
     * @return rolePermissionDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public ResponseEntity<ResponseDTO > getUserPermissionsAndRolePermissions(){

        // getting user role by user id
        UUID userId = appUtils.getAuthenticatedUserId();
        Optional<UserRoleDTO> roleOptional = userRoleRepo.findUserRoleByUserId(userId);
        if (roleOptional.isEmpty()){
            ResponseDTO response = AppUtils.getResponseDto("role record not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        List<RolePermissionsDTO> userPermissions = userPermissionRepo.getUserPermissionsAndRolePermissions(userId, roleOptional.get().getRole());
        if (userPermissions.isEmpty()){
            ResponseDTO  response = AppUtils.getResponseDto("no permission record found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        // fetching permission setups from the db and comparing it with user permissions
        // status of permission will be true in matching cases
        List<PermissionSetUp> permissionSetUps = permissionSetUpRepo.findAll();
        List<Object> permissions = new ArrayList<>();
        for (PermissionSetUp permissionSetup:permissionSetUps){
            Map<String, Object> permissionResult = new HashMap<>();
            boolean permissionPresent = userPermissions.stream().anyMatch(permission->permission.getPermission().equalsIgnoreCase(permissionSetup.getName()));
            permissionResult.put("permission", permissionSetup.getName());
            if (permissionPresent){
                permissionResult.put("status", true);
            }else {
                permissionResult.put("status", false);
            }
            permissions.add(permissionResult);
        }
        ResponseDTO response = AppUtils.getResponseDto("permissions", HttpStatus.OK, permissions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method is used to get user permissions and the user role permissions and cache them for subsequent use.
     * @return rolePermissionDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @Cacheable(value = "userPermissions", key = "#userId")
    public List<RolePermissionsDTO> getPermissions(UUID userId, String role){
        System.out.println("Calling DB for permissions...");
        return userPermissionRepo.getUserPermissionsAndRolePermissions(userId, role);
    }
}