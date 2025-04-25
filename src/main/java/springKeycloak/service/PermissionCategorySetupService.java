package springKeycloak.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import springKeycloak.dto.ResponseDTO;
import springKeycloak.models.setup.PermissionCategorySetup;
import springKeycloak.repositories.PermissionCategorySetupRepo;
import springKeycloak.utils.AppUtils;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class PermissionCategorySetupService {

    private final PermissionCategorySetupRepo permissionCategorySetupRepo;
    private final AppUtils appUtils;

    @Autowired
    public PermissionCategorySetupService(PermissionCategorySetupRepo permissionCategorySetupRepo, AppUtils appUtils) {
        this.permissionCategorySetupRepo = permissionCategorySetupRepo;
        this.appUtils = appUtils;
    }

    /**
     * This is used to fetch all permission setup categories.
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 23rd April 2025
     */
    public ResponseEntity<ResponseDTO> getAll(){
        List<PermissionCategorySetup> permissionCategorySetups = permissionCategorySetupRepo.findAll();

        if (permissionCategorySetups.isEmpty()){
            ResponseDTO response = AppUtils.getResponseDto("no record found", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        ResponseDTO response = AppUtils.getResponseDto("setup created", HttpStatus.OK, permissionCategorySetups);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * This is used to save new permission setup category
     * @param payload
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 23rd April 2025
     */
    public ResponseEntity<ResponseDTO> save(PermissionCategorySetup payload){
        if (payload != null){
            payload.setCreatedBy(appUtils.getAuthenticatedUserId());
            payload.setCreatedAt(ZonedDateTime.now());
        }
        PermissionCategorySetup permissionCategorySetup = permissionCategorySetupRepo.save(payload);
        if (permissionCategorySetup != null){
            ResponseDTO response = AppUtils.getResponseDto("setup created", HttpStatus.CREATED, permissionCategorySetup);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }

        ResponseDTO response = AppUtils.getResponseDto("invalid payload", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}