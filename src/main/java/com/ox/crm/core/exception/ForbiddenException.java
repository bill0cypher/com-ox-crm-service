package com.ox.crm.core.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RestException {
  public ForbiddenException() {
    super(HttpStatus.FORBIDDEN);
  }
}
