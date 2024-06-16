package com.ox.crm.core.mapper;

import com.ox.crm.core.dto.param.TaskCreateParam;
import com.ox.crm.core.dto.param.TaskUpdateParam;
import com.ox.crm.core.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public interface TaskMapper {
  Task mapToTask(TaskCreateParam taskCreateParam);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  Task updateTask(TaskUpdateParam taskUpdateParam, @MappingTarget Task task);
}
