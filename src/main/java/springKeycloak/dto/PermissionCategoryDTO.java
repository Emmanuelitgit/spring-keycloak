package springKeycloak.dto;

import org.springframework.stereotype.Component;

@Component
public class PermissionCategoryDTO {
    private String category;
    private String permission;

    public PermissionCategoryDTO() {
    }

    public PermissionCategoryDTO(String category, String permission) {
        this.category = category;
        this.permission = permission;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
