package springKeycloak.models.setup;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "permission_setup_tb")
public class PermissionSetUp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private UUID categoryId;
    private ZonedDateTime createdAt;
    private UUID createdBy;

    public PermissionSetUp() {
    }

    public PermissionSetUp(ZonedDateTime createdAt, UUID createdBy, UUID id, String name, UUID categoryId) {
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
}