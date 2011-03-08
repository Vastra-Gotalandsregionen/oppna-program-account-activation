package se.vgregion.activation.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import se.vgregion.activation.formbeans.PasswordFormBean;

public class DominoLoginValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordFormBean form = (PasswordFormBean) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "vgrId", "code.missing",
                "Användarnamn får inte lämnas tomt");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dominoPassword", "code.missing",
                "Lösenord får inte lämnas tomt");

        if (!errors.hasErrors()) {
            if ("error".equalsIgnoreCase(form.getVgrId())) {
                errors.reject("code.illegal", "Illegal username");
            }
            // TODO Real validation of domino login using domino ws
        }
    }

}
