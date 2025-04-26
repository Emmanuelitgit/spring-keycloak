package springKeycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springKeycloak.dto.PermissionCategoryDTO;
import springKeycloak.models.setup.PermissionSetUp;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionSetUpRepo extends JpaRepository<PermissionSetUp, UUID> {

    @Query(value = "SELECT ct.name AS category, ps.name AS permission FROM permission_setup_tb ps " +
            "JOIN permission_category_setup_tb ct ON ps.category_id=ct.id;", nativeQuery = true)
    List<PermissionCategoryDTO> getPermissionSetUpsAndCategory();
}
