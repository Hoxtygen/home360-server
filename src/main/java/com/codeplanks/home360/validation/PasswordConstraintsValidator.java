package com.codeplanks.home360.validation;

import com.codeplanks.home360.validation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;

public class PasswordConstraintsValidator implements ConstraintValidator<Password,
        String> {
  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null) {
      return false;
    }
    PasswordValidator passwordValidator = new PasswordValidator(
            Arrays.asList(
                    new LengthRule(10, 300),
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),
                    new CharacterRule(EnglishCharacterData.Digit, 1),
                    new CharacterRule(EnglishCharacterData.Special, 1),
                    new WhitespaceRule()
            ));
    RuleResult result = passwordValidator.validate(new PasswordData(password));
    if (result.isValid()) {
      return true;
    }
    // Sending one message each time failed validation
    context.buildConstraintViolationWithTemplate(passwordValidator
                   .getMessages(result)
                   .stream()
                   .findFirst()
                   .get())
           .addConstraintViolation()
           .disableDefaultConstraintViolation();
    return false;
  }
}
