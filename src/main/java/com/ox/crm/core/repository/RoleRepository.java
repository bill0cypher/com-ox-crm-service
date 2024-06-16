package com.ox.crm.core.repository;

import java.util.Optional;
import java.util.UUID;

import com.ox.crm.core.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
  Optional<Role> findByName(com.ox.crm.core.model.enums.Role name);
}
