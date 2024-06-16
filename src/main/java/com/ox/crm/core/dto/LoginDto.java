package com.ox.crm.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDto {
  @Email
  @NotBlank
  private String email;
  @NotBlank
  private String password;
}
