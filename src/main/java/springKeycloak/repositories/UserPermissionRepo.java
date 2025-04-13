package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
}
