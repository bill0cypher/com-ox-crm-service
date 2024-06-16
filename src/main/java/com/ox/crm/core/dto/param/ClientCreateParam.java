package com.ox.crm.core.dto.param;

import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client details for creation")
public class ClientCreateParam {
  @NotBlank
  @ValidateSpecialCharacters
  @Schema(description = "Name of the company", example = "Tango Inc.")
  private String companyName;
  @ValidateSpecialCharacters
  @Schema(description = "Industry where customer specializes", example = "IT Consulting")
  private String industry;
  @Valid
  @NotNull
  @Schema(description = "Customer's address")
  private AddressCreateParam address;
}
