package com.ox.crm.core.service;

import static com.ox.crm.core.constants.AppConstants.Logging.CONTACT_ID;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.ox.crm.core.dto.ContactDto;
import com.ox.crm.core.dto.param.ContactCreateParam;
import com.ox.crm.core.dto.param.ContactUpdateParam;
import com.ox.crm.core.exception.ConflictException;
import com.ox.crm.core.exception.NotFoundException;
import com.ox.crm.core.mapper.ContactMapper;
import com.ox.crm.core.model.Contact;
import com.ox.crm.core.model.enums.Role;
import com.ox.crm.core.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {
  private final ContactRepository contactRepository;
  private final BCryptPasswordEncoder passwordEncoder;
  private final ContactMapper contactMapper;
  private final RoleService roleService;

  public Contact create(ContactCreateParam input) {
    try {
      var contact = contactMapper.mapToContact(input, passwordEncoder.encode(input.getPassword()));
      var role = roleService.findByName(Role.ROLE_USER);

      contact.setRoles(new HashSet<>(Set.of(role)));
      contact.setPassword(passwordEncoder.encode(input.getPassword()));

      return contactRepository.save(contact);
    } catch (DuplicateKeyException ex) {
      log.error("Contact violated unique constraints: {}", ex.getMessage());
      throw new ConflictException();
    }
  }

  @CacheEvict(value = "contactsCache", key = "#contactId", cacheManager = "cacheManager")
  public Contact update(UUID contactId, ContactUpdateParam contactUpdateParam) {
    var existingContact = findById(contactId);
    var updatedContact = contactMapper.updateContact(contactUpdateParam, existingContact);

    log.info("Contact updated: {}={}", CONTACT_ID, contactId);

    return updatedContact;
  }

  @CacheEvict(value = "contactsCache", key = "#contactId", cacheManager = "cacheManager")
  public void delete(UUID contactId) {
    var contact = findById(contactId);

    contactRepository.delete(contact);

    log.info("Contact deleted: {}", CONTACT_ID, contactId);
  }

  @Cacheable(value = "contactsCache", key = "#contactId", cacheManager = "cacheManager")
  public Contact findById(UUID contactId) {
    return contactRepository.findById(contactId)
        .orElseThrow(() -> {
          log.error("Contact with id {} not found", CONTACT_ID, contactId);
          return new NotFoundException();
        });
  }

  public Contact findByEmail(String email) {
    return contactRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.error("Contact not found by given email");
          return new NotFoundException();
        });
  }

  public Page<ContactDto> findAll(Pageable pageable) {
    var contacts = contactRepository.findAll(pageable);
    var contactsMapped = contacts.getContent()
        .stream()
        .map(contactMapper::mapToContactDto)
        .toList();

    return PageableExecutionUtils.getPage(contactsMapped, contacts.getPageable(), contacts::getTotalElements);
  }
}
