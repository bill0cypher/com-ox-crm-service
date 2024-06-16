package com.ox.crm.core.advice;

import java.util.Optional;

import com.ox.crm.core.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ValidationExceptionHandler {
  private final MessageSource messageSource;

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleParameterValidationException(ConstraintViolationException exception) {
    return new ResponseEntity<>(buildErrorResponse(exception), HttpStatus.BAD_REQUEST);
  }

  private ErrorResponse buildErrorResponse(ConstraintViolationException exception) {
    var errors = exception.getConstraintViolations()
        .stream()
        .map(this::buildErrorDetail)
        .toList();

    return new ErrorResponse(errors);
  }

  private ErrorResponse.ErrorDetails buildErrorDetail(final ConstraintViolation<?> constraintViolation) {

    var message = constraintViolation.getMessage();

    var fieldName = Optional.ofNullable(constraintViolation.getPropertyPath())
        .map(Path::toString)
        .orElse(null);

    return ErrorResponse.ErrorDetails.builder()
        .field(fieldName)
        .message(message)
        .build();
  }
}
