package se.vgregion.activation.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import se.vgregion.activation.formbeans.PasswordFormBean;

public class PasswordMatchValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordFormBean form = (PasswordFormBean) target;

        String password = form.getPassword();
        String passwordCheck = form.getPasswordCheck();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "code.missing",
                "Lösenord får inte lämnas tomt");

        if (password != null && !password.equals(passwordCheck)) {
            errors.rejectValue("passwordCheck", "code.fieldmatch",
                    "Dina lösenord stämmer inte överrens. Försök igen.");
        }
    }
}