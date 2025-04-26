package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springKeycloak.dto.UserResponseDTO;
import springKeycloak.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findUserByUsername(String username);

    @Query(value = "SELECT u.first_name, u.last_name, u.username, u.email, rs.name AS role, ps.name AS permission FROM user_permission_tb up " +
            "JOIN permission_setup_tb ps ON ps.id=up.permission_id " +
            "JOIN user_tb u ON u.id=up.user_id " +
            "JOIN user_role_tb ur ON ur.user_id = u.id " +
            "JOIN role_setup_tb rs ON rs.id = ur.role_id ", nativeQuery = true)
    List<UserResponseDTO> getUserDetails();

    Optional<User> findUserByEmail(String email);
}
