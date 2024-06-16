package com.ox.crm.core.validation;

import com.ox.crm.core.validation.annotations.ValidateEnumValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidateEnumValue, String> {

  private ValidateEnumValue annotation;

  @Override
  public void initialize(ValidateEnumValue constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    annotation = constraintAnnotation;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    boolean result = false;
    final Object[] enumValues = this.annotation.enumClass().getEnumConstants();
    if (value == null || value.isEmpty() || value.isBlank()) {
      result = true;
    } else if (enumValues != null) {
      for (Object enumValue : enumValues) {
        if (value.equals(enumValue.toString())) {
          result = true;
          break;
        }
      }
    }
    return result;
  }
}
