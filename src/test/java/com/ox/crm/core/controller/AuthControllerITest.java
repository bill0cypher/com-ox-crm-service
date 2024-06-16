package com.ox.crm.core.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.dto.LoginDto;
import com.ox.crm.core.dto.LoginResponse;
import com.ox.crm.core.dto.param.ContactCreateParam;
import com.ox.crm.core.model.Contact;
import com.ox.crm.core.model.Privilege;
import com.ox.crm.core.model.Role;
import com.ox.crm.core.repository.ContactRepository;
import com.ox.crm.core.repository.PrivilegeRepository;
import com.ox.crm.core.repository.RoleRepository;
import com.ox.crm.core.repository.TaskRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest()
@TestPropertySource("classpath:application-test.yaml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class AuthControllerITest extends BaseControllerITTest {

  @Autowired
  AuthControllerITest(
      BCryptPasswordEncoder passwordEncoder,
      ObjectMapper objectMapper,
      PrivilegeRepository privilegeRepository,
      RoleRepository roleRepository,
      ContactRepository contactRepository,
      TaskRepository taskRepository,
      MockMvc mockMvc) {
    super(passwordEncoder, objectMapper, privilegeRepository, roleRepository, contactRepository, taskRepository, mockMvc);
  }

  @BeforeEach
  void setUp() {
    var privilege = Privilege.builder()
        .name("READ")
        .build();
    var savedPrivilege = privilegeRepository.save(privilege);
    var role = Role.builder()
        .name(com.ox.crm.core.model.enums.Role.ROLE_USER)
        .privileges(Set.of(savedPrivilege))
        .build();

    roleRepository.save(role);
  }

  @AfterEach
  void destroy() {
    teardown();
  }

  @Test
  @SneakyThrows
  void shouldLogin() {
    var userPassword = "11111111";
    var savedRole = roleRepository.findByName(com.ox.crm.core.model.enums.Role.ROLE_USER).get();
    principalContact = contactRepository.save(
        Contact.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@doe.com")
            .phone("+4-878-9891")
            .roles(new HashSet<>(Set.of(savedRole)))
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

    assertThat(response.getAccessToken()).isNotEmpty();
  }

  @Test
  @SneakyThrows
  void shouldSignup() {
    var contactParam = ContactCreateParam.builder()
        .firstName("Samuel")
        .lastName("Gringo")
        .phone("+2-222-3333")
        .email("samuel@gmail.com")
        .password("11111111")
        .build();
    mockMvc.perform(post("/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(contactParam)))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(contactParam.getFirstName()));
  }
}
