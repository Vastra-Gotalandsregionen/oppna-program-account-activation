package se.vgregion.activation.validators;

import javax.annotation.Resource;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import se.vgregion.account.services.AccountService;
import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.activation.formbeans.PasswordFormBean;

public class OTPLoginValidator implements Validator {
    @Resource
    AccountService accountService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordFormBean form = (PasswordFormBean) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oneTimePassword", "code.missing",
                "Activation code is missing");

        if (!errors.hasErrors()) {
            OneTimePassword account = accountService.getAccount(new PublicHash(form.getOneTimePassword()));
            if (account == null) {
                errors.rejectValue("oneTimePassword", "code.illegal", "Felaktig aktiveringskod");
            } else if (!account.isValid()) {
                errors.rejectValue("oneTimePassword", "code.invalid", "Aktiveringskoden har redan använts");
            } else if (account.hasExpired()) {
                errors.rejectValue("oneTimePassword", "code.expired", "Aktiveringskoden är för gammal");
            }
        }
    }

}
