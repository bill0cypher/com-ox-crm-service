package com.ox.crm.core.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.ox.crm.core.model.Task;
import com.ox.crm.core.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
  Optional<Task> findByIdAndStatusIn(UUID name, Set<Status> status);
}
