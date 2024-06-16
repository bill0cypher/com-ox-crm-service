package com.ox.crm.core.controller;

import static com.ox.crm.core.constants.AppConstants.BEARER_PREFIX;
import static com.ox.crm.core.model.enums.Status.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ox.crm.core.dto.param.TaskCreateParam;
import com.ox.crm.core.dto.param.TaskUpdateParam;
import com.ox.crm.core.model.Task;
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
class TaskControllerITTest extends BaseControllerITTest {

  private Task persistedTask;

  @Autowired
  TaskControllerITTest(BCryptPasswordEncoder passwordEncoder,
      ObjectMapper objectMapper,
      PrivilegeRepository privilegeRepository,
      RoleRepository roleRepository,
      ContactRepository contactRepository,
      TaskRepository taskRepository,
      MockMvc mockMvc) {
    super(passwordEncoder, objectMapper, privilegeRepository, roleRepository, contactRepository, taskRepository, mockMvc);
  }

  @BeforeEach
  @SneakyThrows
  void setUp() {
    setup();
    persistedTask = taskRepository.save(Task.builder()
        .description("Task#1")
        .status(OPEN)
        .duration(Duration.ofDays(1))
        .build());
  }

  @AfterEach
  void destroy() {
    teardown();
  }

  @Test
  @SneakyThrows
  void shouldCreateTask() {
    var authHeader = new HttpHeaders();
    var requestBody = TaskCreateParam.builder()
        .duration(Duration.ofDays(1))
        .status("OPEN")
        .description("Task2")
        .build();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.post("/v1/tasks")
            .headers(authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(requestBody.getDescription()));
  }

  @Test
  @SneakyThrows
  void shouldUpdateTask() {
    var authHeader = new HttpHeaders();
    var requestBody = TaskUpdateParam.builder()
        .duration(Duration.ofDays(2))
        .status("IN_PROGRESS")
        .description("Task2")
        .build();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.patch("/v1/tasks/" + persistedTask.getId())
            .headers(authHeader)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(requestBody.getDescription()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(requestBody.getStatus()));
  }

  @Test
  @SneakyThrows
  void shouldAssignTask() {
    var authHeader = new HttpHeaders();
    var taskId = persistedTask.getId();
    var contactId = principalContact.getId();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.patch("/v1/tasks/assign/" + taskId.toString())
            .param("contactId", contactId.toString())
            .headers(authHeader)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isAccepted());

    assertThat(taskRepository.findById(taskId)).isNotNull()
        .get()
        .satisfies(task -> {
          assertThat(task.getContact()).isNotNull();
          assertThat(task.getContact().getId()).isEqualTo(contactId);
        });
  }

  @Test
  @SneakyThrows
  void shouldFindAllTasks() {
    var authHeader = new HttpHeaders();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.get("/v1/tasks")
        .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].description").value(persistedTask.getDescription()));
  }

  @Test
  @SneakyThrows
  void shouldFindTaskById() {
    var authHeader = new HttpHeaders();
    var taskId = persistedTask.getId().toString();
    authHeader.set(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token);

    mockMvc.perform(MockMvcRequestBuilders.get("/v1/tasks/" + taskId)
            .headers(authHeader))
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(taskId));
  }
}
