package com.ox.crm.core.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class RestException extends ResponseStatusException {
  public RestException(HttpStatusCode status) {
    super(status);
  }

  public RestException(HttpStatusCode status, String reason) {
    super(status, reason);
  }

  public RestException(int rawStatusCode, String reason, Throwable cause) {
    super(rawStatusCode, reason, cause);
  }

  public RestException(HttpStatusCode status, String reason, Throwable cause) {
    super(status, reason, cause);
  }
}
