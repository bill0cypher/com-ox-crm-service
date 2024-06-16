package com.ox.crm.core.controller;

import static java.util.UUID.fromString;

import com.ox.crm.core.dto.param.TaskCreateParam;
import com.ox.crm.core.dto.param.TaskUpdateParam;
import com.ox.crm.core.mapper.TaskMapper;
import com.ox.crm.core.model.Task;
import com.ox.crm.core.service.TaskService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Tasks resource")
public class TaskController {

  private final TaskService taskService;
  private final TaskMapper taskMapper;

  @PostMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public Task createTask(@Valid @RequestBody TaskCreateParam taskParam) {
    var task = taskMapper.mapToTask(taskParam);

    return taskService.create(task);
  }

  @PatchMapping("{taskId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public Task updateTask(@PathVariable @UUID String taskId, @Valid @RequestBody TaskUpdateParam taskParam) {
    return taskService.updateTask(fromString(taskId), taskParam);
  }

  @PatchMapping("assign/{taskId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void assignTask(@PathVariable @UUID String taskId, @RequestParam("contactId") @UUID String contactId) {
    taskService.assignTask(fromString(taskId), fromString(contactId));
  }

  @GetMapping("{taskId}")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public Task findTaskById(@PathVariable @UUID String taskId) {
    return taskService.findById(fromString(taskId));
  }

  @GetMapping
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created"),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
  })
  public Page<Task> findAllTasks(Pageable pageable) {
    return taskService.findAll(pageable);
  }
}
