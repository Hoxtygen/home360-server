package com.codeplanks.home360.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsBooleanValidator implements ConstraintValidator<IsBoolean, Object> {

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if(value == null){
      return false;
    }
    return value instanceof Boolean;
  }
}
