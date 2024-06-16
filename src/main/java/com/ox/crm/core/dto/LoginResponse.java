package com.ox.crm.core.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
  private String accessToken;
  private long expiresIn;
}
