package alex.tir.storage.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = {PasswordConstraintValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPassword {

    String message() default "password is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
