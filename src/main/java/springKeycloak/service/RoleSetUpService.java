package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.setup.RoleSetUp;
import springKeycloak.repositories.RoleSetUpRepo;
import springKeycloak.utils.AppUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleSetUpService {

    private final RoleSetUpRepo roleSetUpRepo;
    private final AppUtils appUtils;

    @Autowired
    public RoleSetUpService(RoleSetUpRepo roleSetUpRepo, AppUtils appUtils) {
        this.roleSetUpRepo = roleSetUpRepo;
        this.appUtils = appUtils;
    }

    /**
     * This method is used to save a role setup.
     * @param roleSetUp
     * @return roleSetUp object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR')")
    public RoleSetUp saveRole(RoleSetUp roleSetUp){
        roleSetUp.setCreatedAt(ZonedDateTime.now());
        roleSetUp.setCreatedBy(appUtils.getAuthenticatedUserId());
        KeyCloakService.addRoleToKeycloak(roleSetUp.getName());
        return roleSetUpRepo.save(roleSetUp);
    }

    /**
     * This method is used to get all roles.
     * @return roleSetUp object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR')")
    public ResponseEntity<ResponseDTO> getAllRoles(){
        List<RoleSetUp> roleSetUps = roleSetUpRepo.findAll();
        if (roleSetUps.isEmpty()){
            ResponseDTO response = AppUtils.getResponseDto("no role setup record found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        ResponseDTO response = AppUtils.getResponseDto("role setups", HttpStatus.OK, roleSetUps);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This method is used to a role by id.
     * @return rolePermissionDto object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public RoleSetUp getRoleById(UUID id){
        Optional<RoleSetUp> roleSetUpOptional = roleSetUpRepo.findById(id);
        if (roleSetUpOptional.isEmpty()){
            throw new NullPointerException("No role record found");
        }
        return roleSetUpOptional.get();
    }
}
