package springKeycloak.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import springKeycloak.Exception.NotFoundException;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.dto.UserRoleDTO;
import springKeycloak.models.RoleSetUp;
import springKeycloak.models.User;
import springKeycloak.models.UserRole;
import springKeycloak.repositories.UserRoleRepo;
import springKeycloak.utils.AppUtils;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserRoleService {

    private final UserRoleRepo userRoleRepo;
    private final UserService userService;
    private final RoleSetUpService roleSetUpService;
    private final AppUtils appUtils;

    @Autowired
    public UserRoleService(UserRoleRepo userRoleRepo, UserService userService, RoleSetUpService roleSetUpService, AppUtils appUtils) {
        this.userRoleRepo = userRoleRepo;
        this.userService = userService;
        this.roleSetUpService = roleSetUpService;
        this.appUtils = appUtils;
    }

    // this method is used to assign role to a user
    public UserRole saveUserRole(UUID roleId, UUID userId){
        User user = userService.getUserById(userId);
        RoleSetUp roleSetUp = roleSetUpService.getRoleById(roleId);

        if (user !=null && roleSetUp != null){
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreatedAt(ZonedDateTime.now());
            userRole.setCreatedBy(appUtils.getAuthenticatedUserId());
            userRoleRepo.save(userRole);
            return userRole;
        }else {
            throw new NullPointerException("rol or user record not found");
        }
    }

    // this method is used to fetch user role by the user id
    public ResponseDTO getUserRoleByUserId(UUID id){
        Optional<UserRoleDTO> userRoleOptional = userRoleRepo.findUserRoleByUserId(id);
        if (userRoleOptional.isEmpty()){
            return AppUtils.getResponseDto("no role record found", HttpStatus.NOT_FOUND);
        }
        return AppUtils.getResponseDto("user role", HttpStatus.OK, userRoleOptional.get().getRole());
    }
}
