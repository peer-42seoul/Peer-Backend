package peer.backend.validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import peer.backend.annotation.ValidDateTime;

public class DateTimeValidator implements ConstraintValidator<ValidDateTime, LocalDateTime> {

    private String pattern;

    @Override
    public void initialize(ValidDateTime constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.pattern);
        String formattedDateTime = value.format(formatter);

        try {
            LocalDateTime.from(LocalDateTime.parse(formattedDateTime, formatter));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
}
