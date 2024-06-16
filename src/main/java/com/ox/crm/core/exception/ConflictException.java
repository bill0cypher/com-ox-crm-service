package com.ox.crm.core.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends RestException {
  public ConflictException() {
    super(HttpStatus.CONFLICT);
  }
}
