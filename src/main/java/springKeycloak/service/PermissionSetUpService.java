package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import springKeycloak.models.PermissionSetUp;
import springKeycloak.repositories.PermissionSetUpRepo;
import springKeycloak.repositories.UserPermissionRepo;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionSetUpService {

    private final PermissionSetUpRepo permissionSetUpRepo;

    @Autowired
    public PermissionSetUpService(PermissionSetUpRepo permissionSetUpRepo) {
        this.permissionSetUpRepo = permissionSetUpRepo;
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','MANAGE_PERMISSIONS')")
    public PermissionSetUp savePermissionSetup(PermissionSetUp permissionSetUp){
        permissionSetUp.setCreatedAt(ZonedDateTime.now());
        return permissionSetUpRepo.save(permissionSetUp);
    }

    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','MANAGE_PERMISSIONS')")
    public List<PermissionSetUp> getPermissionSetups(){
        return permissionSetUpRepo.findAll();
    }

    public PermissionSetUp getPermissionSetUpById(UUID id){
        Optional<PermissionSetUp> permissionSetUpOptional = permissionSetUpRepo.findById(id);
        if (permissionSetUpOptional.isEmpty()){
            throw new NullPointerException("permission role setup record not found");
        }

        return permissionSetUpOptional.get();
    }
}
