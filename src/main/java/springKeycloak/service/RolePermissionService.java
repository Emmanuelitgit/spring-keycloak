package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springKeycloak.models.PermissionSetUp;
import springKeycloak.models.RolePermission;
import springKeycloak.models.RoleSetUp;
import springKeycloak.models.User;
import springKeycloak.repositories.RolePermissionRepo;
import springKeycloak.utils.AppUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RolePermissionService {

    private final RolePermissionRepo rolePermissionRepo;
    private final UserService userService;
    private final RoleSetUpService roleSetUpService;
    private final PermissionSetUpService permissionSetUpService;
    private final AppUtils appUtils;

    @Autowired
    public RolePermissionService(RolePermissionRepo rolePermissionRepo, UserService userService, RoleSetUpService roleSetUpService, PermissionSetUpService permissionSetUpService, AppUtils appUtils) {
        this.rolePermissionRepo = rolePermissionRepo;
        this.userService = userService;
        this.roleSetUpService = roleSetUpService;
        this.permissionSetUpService = permissionSetUpService;
        this.appUtils = appUtils;
    }

    public List<RolePermission> saveRolePermission(List<UUID> permissions, UUID roleId){

        List<RolePermission> rolePermissions = new ArrayList<>();
        for (UUID permissionId:permissions){
            PermissionSetUp permissionSetUp = permissionSetUpService.getPermissionSetUpById(permissionId);
            RoleSetUp roleSetUp = roleSetUpService.getRoleById(roleId);

            if (permissionSetUp == null || roleSetUp == null){
                throw new NullPointerException("user or role record not found");
            }

            RolePermission rolePermission = new RolePermission();
            rolePermission.setPermissionId(permissionSetUp.getId());
            rolePermission.setRoleId(roleSetUp.getId());
            rolePermission.setCreatedAt(ZonedDateTime.now());
            rolePermission.setCreatedBy(appUtils.getAuthenticatedUserId());
            rolePermissionRepo.save(rolePermission);
            rolePermissions.add(rolePermission);
        }

        return rolePermissions;
    }
}
