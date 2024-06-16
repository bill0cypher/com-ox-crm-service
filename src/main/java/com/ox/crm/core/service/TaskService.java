package com.ox.crm.core.service;

import static com.ox.crm.core.constants.AppConstants.Logging.TASK_ID;

import java.util.Set;
import java.util.UUID;

import com.ox.crm.core.dto.param.TaskUpdateParam;
import com.ox.crm.core.exception.ForbiddenException;
import com.ox.crm.core.exception.NotFoundException;
import com.ox.crm.core.exception.UnauthorizedException;
import com.ox.crm.core.mapper.TaskMapper;
import com.ox.crm.core.model.Task;
import com.ox.crm.core.model.enums.Status;
import com.ox.crm.core.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
  private final TaskRepository taskRepository;
  private final ContactService contactService;
  private final JwtService jwtService;
  private final TaskMapper taskMapper;

  public Task create(Task task) {
    return taskRepository.save(task);
  }

  public void assignTask(UUID taskId, UUID contactId) {
    var task = findTaskByIdAndStatus(taskId, Set.of(Status.OPEN, Status.IN_PROGRESS));
    var contact = contactService.findById(contactId);
    task.setContact(contact);

    taskRepository.save(task);
  }

  @CacheEvict(value = "tasksCache", key = "#taskId", cacheManager = "cacheManager")
  public Task updateTask(UUID taskId, TaskUpdateParam taskParam) {
    var task = findById(taskId);
    try {
      var principalEmail = jwtService.getPrincipal()
          .getUsername();
      var contactId = contactService.findByEmail(principalEmail)
          .getId();

      if (contactId != task.getContact().getId()) {
        throw new UnauthorizedException();
      }

      var taskUpdated = taskMapper.updateTask(taskParam, task);
      return taskRepository.save(taskUpdated);
    } catch (UnauthorizedException e) {
      log.error("Unauthorized user can't update tasks.", e);
      throw new UnauthorizedException();
    } catch (ForbiddenException e) {
      log.error("User lacks of permissions to update this task", e);
      throw new ForbiddenException();
    }
  }

  @Cacheable(value = "tasksCache", key = "#taskId", cacheManager = "cacheManager")
  public Task findById(UUID taskId) {
    return taskRepository.findById(taskId)
        .orElseThrow(() -> {
          log.error("Task not found: {}={}", TASK_ID, taskId);
          return new NotFoundException();
        });
  }

  public Task findTaskByIdAndStatus(UUID taskId, Set<Status> statuses) {
    return taskRepository.findByIdAndStatusIn(taskId, statuses)
        .orElseThrow(() -> {
          log.error("Task not found: {}={}", TASK_ID, taskId);
          return new NotFoundException();
        });
  }

  public Page<Task> findAll(Pageable pageable) {
    return taskRepository.findAll(pageable);
  }
}
