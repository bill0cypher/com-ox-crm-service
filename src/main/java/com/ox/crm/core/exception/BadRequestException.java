package com.ox.crm.core.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends RestException {
  public BadRequestException() {
    super(HttpStatus.BAD_REQUEST);
  }
}
