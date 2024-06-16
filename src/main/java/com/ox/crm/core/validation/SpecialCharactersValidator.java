package com.ox.crm.core.validation;

import static com.ox.crm.core.constants.AppConstants.ALPHA_NUMERIC_REGEX;

import com.ox.crm.core.validation.annotations.ValidateSpecialCharacters;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class SpecialCharactersValidator implements ConstraintValidator<ValidateSpecialCharacters, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (StringUtils.isBlank(value)) {
      return true;
    }
    return value.matches(ALPHA_NUMERIC_REGEX);
  }
}
