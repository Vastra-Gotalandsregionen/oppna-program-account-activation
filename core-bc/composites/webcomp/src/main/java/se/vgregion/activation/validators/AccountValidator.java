package se.vgregion.activation.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import se.vgregion.account.services.AccountService;
import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.activation.formbeans.PasswordFormBean;

public class AccountValidator implements Validator {
    @Autowired
    AccountService accountService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordFormBean form = (PasswordFormBean) target;
        OneTimePassword account = accountService.getAccount(new PublicHash(form.getOneTimePassword()));

        if (account == null) {
            errors.rejectValue("oneTimePassword", "code.illegal", "Activation code does not exist");
        } else if (!account.isValid()) {
            errors.rejectValue("oneTimePassword", "code.invalid", "Activation code has already been used");
        } else if (account.hasExpired()) {
            errors.rejectValue("oneTimePassword", "code.expired", "Activation code has expired");
        }
    }

}
