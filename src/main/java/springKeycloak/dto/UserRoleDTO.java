package springKeycloak.dto;

import lombok.*;


public class UserRoleDTO {
    private String role;

    public UserRoleDTO() {
    }

    public UserRoleDTO(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
