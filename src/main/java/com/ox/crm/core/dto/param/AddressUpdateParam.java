package com.ox.crm.core.dto.param;

import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressUpdateParam {
  @ValidateSpecialCharacters
  @Schema(description = "Client's country", example = "US")
  private String country;
  @ValidateSpecialCharacters
  @Schema(description = "Client's city", example = "Chicago")
  private String city;
  @ValidateSpecialCharacters
  @Schema(description = "Client's state", example = "Illinois")
  private String state;
  @ValidateSpecialCharacters
  @Schema(description = "Postal code", example = "60007")
  private String zipCode;
  @ValidateSpecialCharacters
  @Schema(description = "Full address line", example = "123 Main Street, apt 4B San Diego CA, 91911")
  private String addressLine1;
  @Schema(
      description = "Full address line",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED,
      example = "123 Main Street, apt 4B San Diego CA, 91911")
  private String addressLine2;
}
