package com.ox.crm.core.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.dto.LoginDto;
import com.ox.crm.core.dto.LoginResponse;
import com.ox.crm.core.model.Contact;
import com.ox.crm.core.model.Privilege;
import com.ox.crm.core.model.Role;
import com.ox.crm.core.repository.ContactRepository;
import com.ox.crm.core.repository.PrivilegeRepository;
import com.ox.crm.core.repository.RoleRepository;
import com.ox.crm.core.repository.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@AllArgsConstructor
public class BaseControllerITTest {
  
  protected MockMvc mockMvc;
  
  protected TaskRepository taskRepository;
  
  protected ContactRepository contactRepository;
  
  protected RoleRepository roleRepository;
  
  protected PrivilegeRepository privilegeRepository;
  
  protected ObjectMapper objectMapper;

  protected PasswordEncoder passwordEncoder;

  protected String token;

  protected Contact principalContact;

  private Set<Role> roles = new HashSet<>();

  public BaseControllerITTest(
      BCryptPasswordEncoder passwordEncoder,
      ObjectMapper objectMapper,
      PrivilegeRepository privilegeRepository,
      RoleRepository roleRepository,
      ContactRepository contactRepository,
      TaskRepository taskRepository,
      MockMvc mockMvc) {
    this.passwordEncoder = passwordEncoder;
    this.objectMapper = objectMapper;
    this.privilegeRepository = privilegeRepository;
    this.roleRepository = roleRepository;
    this.contactRepository = contactRepository;
    this.taskRepository = taskRepository;
    this.mockMvc = mockMvc;
  }

  @SneakyThrows
  protected void setup() {
    var userPassword = "11111111";
    var privilege = Privilege.builder()
        .name("READ")
        .build();
    var savedPrivilege = privilegeRepository.save(privilege);
    var role = Role.builder()
        .name(com.ox.crm.core.model.enums.Role.ROLE_USER)
        .privileges(Set.of(savedPrivilege))
        .build();

    var savedRole = roleRepository.save(role);
    roles.add(savedRole);
    principalContact = contactRepository.save(
        Contact.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@doe.com")
            .phone("+4-878-9891")
            .roles(roles)
            .password(passwordEncoder.encode(userPassword))
            .build());
    var loginParam = new LoginDto(principalContact.getEmail(), userPassword);
    var resultActions = mockMvc.perform(post("/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginParam)));
    byte[] loginResponse = resultActions.andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsByteArray();
    LoginResponse response = objectMapper.readValue(loginResponse, LoginResponse.class);
    token = response.getAccessToken();
  }

  void teardown() {
    taskRepository.deleteAll();
    contactRepository.deleteAll();
    roleRepository.deleteAll();
    privilegeRepository.deleteAll();
  }

  protected void stubAdminRoleOnPrincipal() {
    var privileges = List.of(
        Privilege.builder()
            .name("READ")
            .build(),
        Privilege.builder()
            .name("WRITE")
            .build(),
        Privilege.builder()
            .name("DELETE")
            .build()
    );
    var savedPrivileges = privilegeRepository.saveAll(privileges);
    var role = Role.builder()
        .name(com.ox.crm.core.model.enums.Role.ROLE_ADMIN)
        .privileges(new HashSet<>(savedPrivileges))
        .build();
    var savedRole = roleRepository.save(role);
    roles.add(savedRole);
    Contact updated = contactRepository.findById(principalContact.getId()).get();
    updated.getRoles().add(savedRole);

    principalContact = contactRepository.save(updated);
  }
}
