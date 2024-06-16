package com.ox.crm.core.repository;

import java.util.Optional;
import java.util.UUID;

import com.ox.crm.core.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {
  Optional<Contact> findByEmail(String email);
}
