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

    /**
     * This method is used to save permission setups.
     * @param permissionSetUp
     * @return permissionSetup object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','MANAGE_PERMISSIONS')")
    public PermissionSetUp savePermissionSetup(PermissionSetUp permissionSetUp){
        permissionSetUp.setCreatedAt(ZonedDateTime.now());
        return permissionSetUpRepo.save(permissionSetUp);
    }

    /**
     * This method is used to fetch list of permission setups.
     * @return list of permission setups
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','MANAGE_PERMISSIONS')")
    public List<PermissionSetUp> getPermissionSetups(){
        return permissionSetUpRepo.findAll();
    }

    /**
     * This method is used to get permission setup given the id.
     * @return permissionSetup object
     * @auther Emmanuel Yidana
     * @createdAt 16h April 2025
     */
    public PermissionSetUp getPermissionSetUpById(UUID id){
        Optional<PermissionSetUp> permissionSetUpOptional = permissionSetUpRepo.findById(id);
        if (permissionSetUpOptional.isEmpty()){
            throw new NullPointerException("permission role setup record not found");
        }
        return permissionSetUpOptional.get();
    }
}
