package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springKeycloak.dto.RolePermissionsDTO;
import springKeycloak.models.RolePermission;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepo extends JpaRepository<RolePermission, UUID> {

    @Query(value = "SELECT ps.name AS permission FROM role_permission_tb rp " +
            "JOIN permission_setup_tb ps ON ps.id = rp.permission_id " +
            "JOIN role_setup_tb rs ON rs.id = rp.role_id " +
            "WHERE rs.name = ?", nativeQuery = true)
    List<RolePermissionsDTO> findRolePermissionsByRoleName(String role);

}
