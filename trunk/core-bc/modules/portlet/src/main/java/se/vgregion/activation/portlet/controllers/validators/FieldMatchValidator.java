package se.vgregion.activation.portlet.controllers.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import se.vgregion.activation.portlet.controllers.formbeans.PasswordFormBean;

public class FieldMatchValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        System.out.println("FieldMatchValidator.validate()");
        PasswordFormBean form = (PasswordFormBean) target;
        String password = form.getPassword();
        String passwordCheck = form.getPasswordCheck();

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required", "FELFELFEL1");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordCheck", "required", "FELFELFEL2");
        if (password != null && !password.equals(passwordCheck)) {
            errors.rejectValue("passwordCheck", "fieldmatch", "FELFELFEL3");
        }
    }
}