package com.ox.crm.core.dto.param;

import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Client details to update")
public class ClientUpdateParam {
  @ValidateSpecialCharacters
  @NotBlank
  @Schema(description = "Name of the company", example = "Tango Inc.")
  private String companyName;
  @ValidateSpecialCharacters
  @Schema(description = "Industry where customer specializes", example = "Tango Inc.")
  private String industry;
  @Valid
  @Schema(description = "Customer's address")
  private AddressUpdateParam address;
}
