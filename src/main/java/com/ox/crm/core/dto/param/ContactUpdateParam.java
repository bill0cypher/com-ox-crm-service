package com.ox.crm.core.dto.param;

import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Contact details to update")
public class ContactUpdateParam {
  @Size(min = 2, max = 50)
  @ValidateSpecialCharacters
  @Schema(description = "First name")
  private String firstName;
  @Size(min = 2, max = 50)
  @ValidateSpecialCharacters
  @Schema(description = "Last name")
  private String lastName;
  @Email
  @Schema(description = "E-Mail", example = "exampl@mail.com")
  private String email;
  @Schema(description = "Phone number", example = "+48 22 1234567")
  private String phone;
}
