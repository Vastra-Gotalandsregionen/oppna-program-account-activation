package se.vgregion.activation.portlet.controllers;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import se.vgregion.activation.domain.form.PasswordFormBean;

public class PasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // PasswordFormBean validator = (PasswordFormBean) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required", "FELFELFEL");
    }

}
