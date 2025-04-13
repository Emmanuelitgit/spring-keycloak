package springKeycloak.dto;

public class UserPermissionDTO {
    private String permission;
    private String username;

    public UserPermissionDTO() {
    }

    public UserPermissionDTO(String permission, String username) {
        this.permission = permission;
        this.username = username;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
