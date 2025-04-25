package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springKeycloak.models.setup.PermissionCategorySetup;

import java.util.UUID;

@Repository
public interface PermissionCategorySetupRepo extends JpaRepository<PermissionCategorySetup, UUID> {
}
