package se.vgregion.activation.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.vgregion.activation.formbeans.PasswordFormBean;

public class PasswordStrengthValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordFormBean form = (PasswordFormBean) target;

        String password = form.getPassword();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "code.missing",
                "Lösenord får inte lämnas tomt");


        if (!errors.hasErrors()) {
            final int i = 6;
            if (password.length() < i) {
                errors.rejectValue("password", "code.password.length", "Lösenordet måste vara minst sex tecken");
            }
            if (!password.matches("[a-zA-Z0-9]*")) {
                errors.rejectValue("password", "code.password.illegal.characters", "Lösenordet får bara innehålla "
                        + "bokstäver och siffror");
            }
            if (!(password.matches(".*[a-zA-Z]+.*") && password.matches(".*[0-9]+.*"))) {
                errors.rejectValue("password", "code.password.min.complexity", "Lösenordet måste innehålla "
                        + "både bokstäver och siffror");
            }
        }
    }
}