package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springKeycloak.dto.UserRoleDTO;
import springKeycloak.models.UserRole;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, UUID> {
    @Query(value = "SELECT rs.name AS role FROM user_role_tb ur " +
            "JOIN user_tb u ON ur.user_id = u.id " +
            "JOIN role_setup_tb rs ON rs.id = ur.role_id " +
            "WHERE u.id=? ", nativeQuery = true)
    Optional<UserRoleDTO> findUserRoleByUserId(UUID userId);
}
