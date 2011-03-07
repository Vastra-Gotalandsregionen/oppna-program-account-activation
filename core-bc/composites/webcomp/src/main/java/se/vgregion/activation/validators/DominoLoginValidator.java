package se.vgregion.activation.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import se.vgregion.activation.formbeans.PasswordFormBean;

public class DominoLoginValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // TODO Validate domino login
    }

}
