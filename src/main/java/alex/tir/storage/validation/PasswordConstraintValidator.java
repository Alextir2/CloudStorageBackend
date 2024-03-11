package alex.tir.storage.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return true;
        }

        PasswordValidator passwordValidator = new PasswordValidator(List.of(
                new WhitespaceRule(),
                new LengthRule(5, 30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1)));

        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        } else {
            StringBuilder errorMessageBuilder = new StringBuilder();
            List<String> errorMessageList = passwordValidator.getMessages(result);
            for (int i = 0; i < errorMessageList.size(); i++) {
                errorMessageBuilder.append(errorMessageList.get(i));
                if (i != errorMessageList.size() - 1) {
                    errorMessageBuilder.append(" ");
                }
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errorMessageBuilder.toString()).addConstraintViolation();
            return false;
        }
    }

}
