package se.vgregion.activation.domain.validation;

import se.vgregion.activation.domain.form.PasswordFormBean;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckPasswordValidator implements ConstraintValidator<CheckPassword, PasswordFormBean> {
    @Override
    public void initialize(CheckPassword checkPassword) {
    }

    @Override
    public boolean isValid(PasswordFormBean passwordFormBean, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("Hahaha");
        if (passwordFormBean  == null) return false;

        if (passwordFormBean.getVgrId() == null) return false;
        if (passwordFormBean.getPassword() == null) return false;

        return (passwordFormBean.getPassword().equals(passwordFormBean.getPasswordCheck()));
    }
}
