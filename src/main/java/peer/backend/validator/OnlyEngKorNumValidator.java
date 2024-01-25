package peer.backend.validator;

import peer.backend.annotation.OnlyEngKorNum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OnlyEngKorNumValidator implements ConstraintValidator<OnlyEngKorNum, String> {

    @Override
    public void initialize(OnlyEngKorNum constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return value.matches("^[a-zA-Z가-힣0-9]+$");
    }
}
