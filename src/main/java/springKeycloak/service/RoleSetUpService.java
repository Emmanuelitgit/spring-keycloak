package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import springKeycloak.models.RoleSetUp;
import springKeycloak.repositories.RoleSetUpRepo;
import springKeycloak.utils.AppUtils;

import javax.management.relation.Role;
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

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR')")
    public RoleSetUp saveRole(RoleSetUp roleSetUp){
        roleSetUp.setCreatedAt(ZonedDateTime.now());
        roleSetUp.setCreatedBy(appUtils.getAuthenticatedUserId());
        return roleSetUpRepo.save(roleSetUp);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR')")
    public List<RoleSetUp> getAllRoles(){
        return roleSetUpRepo.findAll();
    }

    public RoleSetUp getRoleById(UUID id){
        Optional<RoleSetUp> roleSetUpOptional = roleSetUpRepo.findById(id);
        if (roleSetUpOptional.isEmpty()){
            throw new NullPointerException("No role record found");
        }

        return roleSetUpOptional.get();
    }
}
