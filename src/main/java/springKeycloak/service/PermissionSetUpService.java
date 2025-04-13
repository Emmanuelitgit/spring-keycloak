package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import springKeycloak.models.PermissionSetUp;
import springKeycloak.repositories.PermissionSetUpRepo;
import springKeycloak.repositories.UserPermissionRepo;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PermissionSetUpService {

    private final PermissionSetUpRepo permissionSetUpRepo;

    @Autowired
    public PermissionSetUpService(PermissionSetUpRepo permissionSetUpRepo) {
        this.permissionSetUpRepo = permissionSetUpRepo;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINSTRATOR')")
    public PermissionSetUp savePermissionSetup(PermissionSetUp permissionSetUp){
        permissionSetUp.setCreatedAt(ZonedDateTime.now());
        return permissionSetUpRepo.save(permissionSetUp);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINSTRATOR')")
    public List<PermissionSetUp> getPermissionSetups(){
        return permissionSetUpRepo.findAll();
    }
}
