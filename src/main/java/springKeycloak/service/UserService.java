package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import springKeycloak.dto.UserPermissionDTO;
import springKeycloak.models.PermissionSetUp;
import springKeycloak.models.User;
import springKeycloak.models.UserPermission;
import springKeycloak.repositories.PermissionSetUpRepo;
import springKeycloak.repositories.UserPermissionRepo;
import springKeycloak.repositories.UserRepository;
import springKeycloak.utils.AppUtils;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PermissionSetUpRepo permissionSetUpRepo;
    private final UserPermissionRepo userPermissionRepo;
    private final AppUtils appUtils;

    @Autowired
    public UserService(UserRepository userRepository, PermissionSetUpRepo permissionSetUpRepo, UserPermissionRepo userPermissionRepo, AppUtils appUtils) {
        this.userRepository = userRepository;
        this.permissionSetUpRepo = permissionSetUpRepo;
        this.userPermissionRepo = userPermissionRepo;
        this.appUtils = appUtils;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINSTRATOR')")
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINSTRATOR')")
    public User saveUser(User userPayload){
//        AppUtils.addUserToKeycloak(userPayload);
        User user = userRepository.save(userPayload);
        return user;
    }

    public User getUserById(UUID id){
        if (appUtils.getAuthenticatedUserId().equals(id) || AppUtils.getAuthenticatedUserAuthorities().contains("SYSTEM ADMINISTRATOR")){
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()){
                throw new NullPointerException("User record not found");
            }
            return userOptional.get();
        }else {
            throw new NullPointerException("Unauthorized");
        }

    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINSTRATOR')")
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
      if (appUtils.getAuthenticatedUserId().equals(userId) || AppUtils.getAuthenticatedUserAuthorities().contains("SYSTEM ADMINISTRATOR")){
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
        String authenticatedUsername = AppUtils.getAuthenticatedUsername();
        if (Objects.equals(authenticatedUsername, username) || AppUtils.getAuthenticatedUserAuthorities().contains("SYSTEM ADMINISTRATOR")){
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
