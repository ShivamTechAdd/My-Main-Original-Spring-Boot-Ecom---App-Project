package com.ecomerce.sbecom.Repository;

import com.ecomerce.sbecom.Model.AppRole;
import com.ecomerce.sbecom.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
