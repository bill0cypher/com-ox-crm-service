package com.ox.crm.core.service;

import com.ox.crm.core.exception.InternalErrorException;
import com.ox.crm.core.model.Role;
import com.ox.crm.core.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
  private final RoleRepository roleRepository;

  public Role findByName(com.ox.crm.core.model.enums.Role name) {
    return roleRepository.findByName(name)
        .orElseThrow(() -> {
          log.error("Requested role is missing: {}", name);
          return new InternalErrorException();
        });
  }
}
