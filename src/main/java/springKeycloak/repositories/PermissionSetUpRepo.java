package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springKeycloak.models.PermissionSetUp;

import java.util.UUID;

@Repository
public interface PermissionSetUpRepo extends JpaRepository<PermissionSetUp, UUID> {
}
