package com.ox.crm.core.controller;

import static com.ox.crm.core.constants.AppConstants.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.dto.param.ContactCreateParam;
import com.ox.crm.core.dto.param.ContactUpdateParam;
import com.ox.crm.core.model.Contact;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest()
@TestPropertySource("classpath:application-test.yaml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class ContactControllerITest extends BaseControllerITTest{

  @Autowired
  ContactControllerITest(
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
    setup();
  }

  @AfterEach
  void destroy() {
    teardown();
  }

  @Test
  @SneakyThrows
  void shouldCreateContact() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    var createParam = ContactCreateParam.builder()
        .firstName("Samuel")
        .lastName("Gringo")
        .phone("+2-222-3333")
        .email("samuel@gmail.com")
        .password("11111111")
        .build();

    mockMvc.perform(MockMvcRequestBuilders.post("/v1/contacts")
            .headers(authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createParam))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(createParam.getEmail()));
  }

  @Test
  @SneakyThrows
  void shouldUpdateContact() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    var updateParam = ContactUpdateParam.builder()
        .firstName("Samuel")
        .lastName("Gringo")
        .phone("+2-222-3333")
        .email("samuel@gmail.com")
        .build();

    mockMvc.perform(MockMvcRequestBuilders.patch("/v1/contacts/" + principalContact.getId())
            .headers(authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateParam))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updateParam.getEmail()));
  }

  @Test
  @SneakyThrows
  void shouldDeleteContact() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
    stubAdminRoleOnPrincipal();

    var contactToBeDeleted = Contact.builder()
        .firstName("John")
        .lastName("Deo")
        .phone("+2-545-2121")
        .password("1111")
        .email("deo@email.com")
        .build();
    var persistedContact = contactRepository.save(contactToBeDeleted);

    mockMvc.perform(MockMvcRequestBuilders.delete("/v1/contacts/" + persistedContact.getId())
            .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    assertEquals(Optional.empty(), contactRepository.findById(persistedContact.getId()));
  }

  @Test
  @SneakyThrows
  void shouldFindContactById() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.get("/v1/contacts/" + principalContact.getId())
            .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(principalContact.getEmail()));
  }

  @Test
  @SneakyThrows
  void shouldFindAllContacts() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.get("/v1/contacts")
            .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].email").value(principalContact.getEmail()));
  }
}
