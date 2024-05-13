package com.codeplanks.home360.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD )
@Constraint(validatedBy = IsBooleanValidator.class)
public @interface IsBoolean {
  String message() default "Field must be a boolean value (true or false)";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}