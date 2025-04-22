package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springKeycloak.dto.RolePermissionsDTO;
import springKeycloak.models.RoleSetUp;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleSetUpRepo extends JpaRepository<RoleSetUp, UUID> {
}
