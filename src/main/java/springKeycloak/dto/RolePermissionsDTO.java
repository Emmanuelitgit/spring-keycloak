package springKeycloak.dto;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RolePermissionsDTO {

    private String permission;

    public RolePermissionsDTO() {
    }

    public RolePermissionsDTO(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermissions(String permission) {
        this.permission = permission;
    }
}
