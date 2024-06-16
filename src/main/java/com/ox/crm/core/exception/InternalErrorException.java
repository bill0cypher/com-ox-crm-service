package com.ox.crm.core.exception;

import org.springframework.http.HttpStatus;

public class InternalErrorException extends RestException {
  public InternalErrorException() {
    super(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
