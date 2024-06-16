package com.ox.crm.core.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RestException {
  public NotFoundException() {
    super(HttpStatus.NOT_FOUND);
  }
}
