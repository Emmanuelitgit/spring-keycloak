package springKeycloak.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import springKeycloak.Exception.InvalidDataException;
import springKeycloak.Exception.NotFoundException;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.dto.UserDTO;
import springKeycloak.dto.UserPermissionDTO;
import springKeycloak.models.PermissionSetUp;
import springKeycloak.models.User;
import springKeycloak.models.UserPermission;
import springKeycloak.models.UserRole;
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
    private UserRoleService userRoleService;

    @Autowired
    public UserService(UserRepository userRepository, PermissionSetUpRepo permissionSetUpRepo, UserPermissionRepo userPermissionRepo, AppUtils appUtils, UserRoleRepo userRoleRepo) {
        this.userRepository = userRepository;
        this.permissionSetUpRepo = permissionSetUpRepo;
        this.userPermissionRepo = userPermissionRepo;
        this.appUtils = appUtils;
        this.userRoleRepo = userRoleRepo;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR', 'VIEW_USER_LIST')")
    public List<User> getUsers(){
        return userRepository.findAll();
    }

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
        userRoleService.saveUserRole(userPayload.getRole(), userData.getId());
        return AppUtils.getResponseDto("record added successfully", HttpStatus.CREATED,userPayload);
    }

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

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','MANAGE_PERMISSIONS')")
    public List<UserPermission> saveUserPermissions(UUID userId, List<UUID> permissionIds){
        List<UserPermission> userPermissions = new ArrayList<>();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new NullPointerException("User record not found");
        }
        for (UUID id:permissionIds){
            UserPermission userPermission = new UserPermission();
            Optional<PermissionSetUp> permissionSetUpOptional = permissionSetUpRepo.findById(id);
            if (permissionSetUpOptional.isPresent()){
                userPermission.setUserId(userId);
                userPermission.setPermissionId(id);
                userPermission.setCreatedAt(ZonedDateTime.now());
                UserPermission permission = userPermissionRepo.save(userPermission);
                userPermissions.add(permission);
            }
        }
        return userPermissions;
    }

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
}
