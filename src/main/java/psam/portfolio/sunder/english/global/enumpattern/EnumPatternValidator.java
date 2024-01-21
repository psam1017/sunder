package psam.portfolio.sunder.english.global.enumpattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class EnumPatternValidator implements ConstraintValidator<EnumPattern, Enum<?>> {

    private Pattern pattern;
    private boolean nullable;

    @Override
    public void initialize(EnumPattern constraintAnnotation) {
        try {
            this.pattern = Pattern.compile(constraintAnnotation.regexp());
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Given regexp is invalid", e);
        }
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (nullable) {
            return value == null || pattern.matcher(value.toString()).matches();
        }
        return value != null && pattern.matcher(value.toString()).matches();
    }
}
