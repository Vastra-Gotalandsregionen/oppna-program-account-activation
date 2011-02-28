package se.vgregion.activation.domain.validation;

import se.vgregion.activation.domain.validation.FieldMatch;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
            final Object secondObj = BeanUtils.getProperty(value, secondFieldName);

            if (firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj)) {

                return true;
            }
            context.disableDefaultConstraintViolation();



            context.buildConstraintViolationWithTemplate("{constraints.fieldmatch}")
                    .addNode(secondFieldName)
                    .addConstraintViolation();

            return false;
        } catch (final Exception ignore) {
            // ignore
        }
        return true;
    }
}