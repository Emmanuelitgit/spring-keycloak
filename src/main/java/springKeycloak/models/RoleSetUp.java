package springKeycloak.models;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "role_setup_tb")
public class RoleSetUp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private ZonedDateTime createdAt;
    private UUID createdBy;

    public RoleSetUp() {
    }

    public RoleSetUp(ZonedDateTime createdAt, UUID createdBy, UUID id, String name) {
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.id = id;
        this.name = name;
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
}
