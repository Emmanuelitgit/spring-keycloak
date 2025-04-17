package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springKeycloak.dto.RolePermissionsDTO;
import springKeycloak.dto.UserPermissionDTO;
import springKeycloak.models.UserPermission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPermissionRepo extends JpaRepository<UserPermission, UUID> {
    @Query(value = "SELECT ps.name AS permission, u.username FROM user_permission_tb up " +
            "JOIN user_tb u ON u.id = up.user_id " +
            "JOIN permission_setup_tb ps ON up.permission_id = ps.id " +
            "WHERE u.id = ?", nativeQuery = true)
    List<UserPermissionDTO> getUserPermissionsByUserId(UUID id);

    @Query(value = "SELECT ps.name AS permission, u.username FROM user_permission_tb up " +
            "JOIN user_tb u ON u.id = up.user_id " +
            "JOIN permission_setup_tb ps ON up.permission_id = ps.id " +
            "WHERE u.username = ?", nativeQuery = true)
    List<UserPermissionDTO> getUserPermissionsByUsername(String username);

    @Query(value = "SELECT ps.name AS permission FROM user_permission_tb up " +
            "JOIN user_tb u ON u.id = up.user_id " +
            "JOIN permission_setup_tb ps ON ps.id = up.permission_id " +
            "WHERE u.id = '09744c65-b633-4ff4-aac8-0e80ceff0ca6' " +
            "UNION " +
            "SELECT ps.name AS permission FROM role_permission_tb rp " +
            "JOIN permission_setup_tb ps ON rp.permission_id = ps.id " +
            "JOIN role_setup_tb rs ON rs.id = rp.role_id " +
            "WHERE rs.name = 'ADMIN'",
            nativeQuery = true)
    List<RolePermissionsDTO> getUserPermissionsAndRolePermissions();

}
