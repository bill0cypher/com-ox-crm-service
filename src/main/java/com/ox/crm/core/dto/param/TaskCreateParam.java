package com.ox.crm.core.dto.param;

import java.time.Duration;

import com.ox.crm.core.model.enums.Status;
import com.ox.crm.core.validation.annotations.ValidateEnumValue;
import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;

@Data
@Builder
@Schema(description = "Task details for creation")
public class TaskCreateParam {
  @ValidateSpecialCharacters
  @Schema(description = "Task description")
  private String description;
  @NotBlank
  @ValidateEnumValue(enumClass = Status.class)
  @Schema(description = "Ongoing status of the task")
  private String status;
  @NotNull
  @DurationMin(hours = 1L)
  @DurationMax(days = 13L)
  @Schema(description = "Task complexity in days")
  private Duration duration;
}
