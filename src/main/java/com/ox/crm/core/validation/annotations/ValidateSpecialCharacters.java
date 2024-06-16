package com.ox.crm.core.validation.annotations;

import static com.ox.crm.core.constants.AppConstants.Messages.SPECIAL_CHARACTERS_FORBIDDEN_MSG;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ox.crm.core.validation.SpecialCharactersValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpecialCharactersValidator.class)
public @interface ValidateSpecialCharacters {
  String message() default SPECIAL_CHARACTERS_FORBIDDEN_MSG;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
