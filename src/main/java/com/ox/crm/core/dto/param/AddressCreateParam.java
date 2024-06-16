package com.ox.crm.core.dto.param;

import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Builder
@Schema(description = "Client address details for creation")
public class AddressCreateParam {
  @ValidateSpecialCharacters
  @NotBlank
  @Schema(description = "Client's country", example = "US")
  private String country;
  @ValidateSpecialCharacters
  @NotBlank
  @Schema(description = "Client's city", example = "Chicago")
  private String city;
  @ValidateSpecialCharacters
  @NotBlank
  @Schema(description = "Client's state", example = "Illinois")
  private String state;
  @ValidateSpecialCharacters
  @Schema(description = "Postal code", example = "60007")
  private String zipCode;
  @NotBlank
  @ValidateSpecialCharacters
  @Schema(description = "Full address line", example = "123 Main Street, apt 4B San Diego CA, 91911")
  private String addressLine1;
  @Schema(
      description = "Full address line",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED,
      example = "123 Main Street, apt 4B San Diego CA, 91911")
  private String addressLine2;
}
