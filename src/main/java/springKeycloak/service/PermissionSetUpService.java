package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import springKeycloak.dto.PermissionCategoryDTO;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.dto.RolePermissionsDTO;
import springKeycloak.models.setup.PermissionSetUp;
import springKeycloak.repositories.PermissionSetUpRepo;
import springKeycloak.repositories.RolePermissionRepo;
import springKeycloak.utils.AppUtils;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class PermissionSetUpService {

    private final PermissionSetUpRepo permissionSetUpRepo;
    private final RolePermissionRepo rolePermissionRepo;

    @Autowired
    public PermissionSetUpService(PermissionSetUpRepo permissionSetUpRepo, RolePermissionRepo rolePermissionRepo) {
        this.permissionSetUpRepo = permissionSetUpRepo;
        this.rolePermissionRepo = rolePermissionRepo;
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
//    @PreAuthorize("hasAnyAuthority('SYSTEM ADMINISTRATOR','MANAGE_PERMISSIONS')")
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

    public ResponseEntity<ResponseDTO> updatePermissionSetup(PermissionSetUp permissionSetUp, UUID id){
        Optional<PermissionSetUp> permissionSetUpOptional = permissionSetUpRepo.findById(id);
        if (permissionSetUpOptional.isEmpty()){
            ResponseDTO response = AppUtils.getResponseDto("permission setup record not found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        PermissionSetUp existingData = permissionSetUpOptional.get();
        System.out.println("CategoryId:========" + permissionSetUp.getCategoryId());
        existingData.setCategoryId(permissionSetUp.getCategoryId());
        PermissionSetUp res = permissionSetUpRepo.save(existingData);

        ResponseDTO response = AppUtils.getResponseDto("permission setup updated successfully", HttpStatus.OK, res);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    /**
     * This method is used to fetch permissions setups and role default permissions given the role name.
     * @param role
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 22nd April 2025
     */
    public ResponseEntity<ResponseDTO> getPermissionSetupsAndRoleDefaultPermissions(String role){
        // loading permission setups from the db
        List<PermissionSetUp> permissionSetups = permissionSetUpRepo.findAll();
        if (permissionSetups.isEmpty()){
            ResponseDTO response = AppUtils.getResponseDto("no permission setup record found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        // loading role permissions from the db
        List<RolePermissionsDTO> rolePermissions = rolePermissionRepo.findRolePermissionsByRoleName(role);

        // we check if the user default role already has a permission which is in the permission setups.
        // we set the status to true in matching cases otherwise false.
        List<Object> responseList = new ArrayList<>();
        for (PermissionSetUp permissionSetup:permissionSetups){
            Map<String, Object> responseObject = new HashMap<>();
            boolean status = rolePermissions.stream()
                    .anyMatch((permission -> permission.getPermission().equals(permissionSetup.getName())));
            if (status){
                responseObject.put("status", true);
            }
            if (!status){
                responseObject.put("status", false);
            }
            responseObject.put("permission", permissionSetup.getName());
            responseList.add(responseObject);
        }

        ResponseDTO response = AppUtils.getResponseDto("permission setups", HttpStatus.OK, responseList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDTO> getPermissionSetUpsAndCategory(){
        List<PermissionCategoryDTO> permissionsAndCategories = permissionSetUpRepo.getPermissionSetUpsAndCategory();
        if (permissionsAndCategories.isEmpty()){
            ResponseDTO response = AppUtils.getResponseDto("no permission setup record found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Map<String, List<String>> res = new HashMap<>();
        for (PermissionCategoryDTO permissionCategory:permissionsAndCategories){
            if (!res.containsKey(permissionCategory.getCategory())){
                res.put(permissionCategory.getCategory(), new ArrayList<>());
            }
            res.get(permissionCategory.getCategory()).add(permissionCategory.getPermission());
        }

        ResponseDTO response = AppUtils.getResponseDto("permission setups", HttpStatus.OK, res);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
