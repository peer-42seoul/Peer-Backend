package peer.backend.validator;

import peer.backend.annotation.CustomSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LineBreakNormalizerValidator implements ConstraintValidator<CustomSize, String> {
    private int min;
    private int max;
    @Override
    public void initialize(CustomSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value,  ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null 값은 다른 방식으로 처리
        }
        // 유효성 검사 대신 문자열 변환 로직만 적용
        int lineBreakerSize = 0;
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\r')
                lineBreakerSize++;
        }
        int valueLength = value.length() - lineBreakerSize;
        return (valueLength >= min && valueLength <= max);
    }
}
