package com.ox.crm.core.controller;

import static com.ox.crm.core.constants.AppConstants.BEARER_PREFIX;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.dto.param.AddressCreateParam;
import com.ox.crm.core.dto.param.AddressUpdateParam;
import com.ox.crm.core.dto.param.ClientCreateParam;
import com.ox.crm.core.dto.param.ClientUpdateParam;
import com.ox.crm.core.exception.NotFoundException;
import com.ox.crm.core.model.Address;
import com.ox.crm.core.model.Client;
import com.ox.crm.core.repository.ClientRepository;
import com.ox.crm.core.repository.ContactRepository;
import com.ox.crm.core.repository.PrivilegeRepository;
import com.ox.crm.core.repository.RoleRepository;
import com.ox.crm.core.repository.TaskRepository;
import com.ox.crm.core.service.ClientService;
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
class ClientControllerITTest extends BaseControllerITTest{

  private final ClientService clientService;
  private final ClientRepository clientRepository;
  private Client persistedClient;

  @Autowired
  ClientControllerITTest(
      BCryptPasswordEncoder passwordEncoder,
      ObjectMapper objectMapper,
      PrivilegeRepository privilegeRepository,
      RoleRepository roleRepository,
      ContactRepository contactRepository,
      TaskRepository taskRepository,
      ClientService clientService,
      ClientRepository clientRepository,
      MockMvc mockMvc) {
    super(passwordEncoder, objectMapper, privilegeRepository, roleRepository, contactRepository, taskRepository, mockMvc);
    this.clientService = clientService;
    this.clientRepository = clientRepository;
  }

  @BeforeEach
  void setUp() {
    setup();

    var address = Address.builder()
        .country("US")
        .city("Chicago")
        .state("Illinois")
        .addressLine1("US Chicago Illinois")
        .zipCode("60007")
        .build();

    persistedClient = clientService.create(
        Client.builder()
            .companyName("Tango Inc.")
            .industry("IT Consulting")
            .address(address)
            .build()
    );
  }

  @AfterEach
  void destroy() {
    teardown();
    clientRepository.deleteAll();
  }

  @Test
  @SneakyThrows
  void shouldCreateClient() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    var createParam = ClientCreateParam.builder()
        .companyName("InCore LLC")
        .address(AddressCreateParam.builder()
            .country("CA")
            .city("Calgary")
            .state("Alberta")
            .zipCode("000000")
            .addressLine1("CA Alberta Calgary 0000")
            .build())
        .build();

    mockMvc.perform(MockMvcRequestBuilders.post("/v1/clients")
            .headers(authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createParam)))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value(createParam.getCompanyName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.address.country").value(createParam.getAddress()
            .getCountry()));
  }

  @Test
  @SneakyThrows
  void shouldUpdateClient() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    var updateParam = ClientUpdateParam.builder()
        .companyName("InCore LLC")
        .address(AddressUpdateParam.builder()
            .country("CA")
            .city("Calgary")
            .state("Alberta")
            .zipCode("000000")
            .addressLine1("CA Alberta Calgary 0000")
            .build())
        .build();

    mockMvc.perform(MockMvcRequestBuilders.patch("/v1/clients/" + persistedClient.getId())
            .headers(authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateParam)))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value(updateParam.getCompanyName()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.address.country").value(updateParam.getAddress()
            .getCountry()));

  }

  @Test
  @SneakyThrows
  void shouldDeleteClient() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);
    stubAdminRoleOnPrincipal();

    mockMvc.perform(MockMvcRequestBuilders.delete("/v1/clients/" + persistedClient.getId())
            .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent());

    assertThrowsExactly(NotFoundException.class, () -> clientService.findById(persistedClient.getId()));
    persistedClient = null;
  }

  @Test
  @SneakyThrows
  void shouldFindClientById() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.get("/v1/clients/" + persistedClient.getId())
            .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.companyName").value(persistedClient.getCompanyName()));
  }

  @Test
  @SneakyThrows
  void shouldFindAllClients() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.get("/v1/clients")
            .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].companyName").value(persistedClient.getCompanyName()));
  }
}
