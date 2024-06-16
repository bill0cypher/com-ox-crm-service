package com.ox.crm.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

  private List<ErrorDetails> errorDetails;

  @AllArgsConstructor
  @Data
  @Builder
  public static class ErrorDetails {
    private String field;
    private String message;
    private String type;
  }
}
