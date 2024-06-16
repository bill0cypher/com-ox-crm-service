package com.ox.crm.core.dto;

import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto {
  @Email
  @NotBlank
  private String email;
  @Size(min = 6, max = 20)
  @NotBlank
  private String password;
  @ValidateSpecialCharacters
  private String fullName;
}
