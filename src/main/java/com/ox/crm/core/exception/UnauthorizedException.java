package com.ox.crm.core.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RestException {
  public UnauthorizedException() {
    super(HttpStatus.UNAUTHORIZED);
  }
}
