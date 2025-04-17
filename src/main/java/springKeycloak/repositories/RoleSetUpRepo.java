package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springKeycloak.models.RoleSetUp;

import java.util.UUID;

@Repository
public interface RoleSetUpRepo extends JpaRepository<RoleSetUp, UUID> {
}
