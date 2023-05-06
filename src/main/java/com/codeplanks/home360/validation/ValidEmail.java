package com.codeplanks.home360.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {
  String message() default "Invalid email!";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
