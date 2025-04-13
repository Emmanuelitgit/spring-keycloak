package springKeycloak.models;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "role_permission_tb")
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID permissionId;
    private UUID roleId;
    private UUID createdBy;
    private ZonedDateTime createdAt;

    public RolePermission() {
    }

    public RolePermission(ZonedDateTime createdAt, UUID createdBy, UUID id, UUID permissionId, UUID roleId) {
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.id = id;
        this.permissionId = permissionId;
        this.roleId = roleId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }
}
