package springKeycloak.dto;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserResponseDTO {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;
    private String permission;

    public UserResponseDTO() {
    }

    public UserResponseDTO(String firstName, String lastName, String username, String email,String role, String permission) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.role = role;
        this.permission = permission;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
