package com.ox.crm.core.repository;

import java.util.UUID;

import com.ox.crm.core.model.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, UUID> {
}
