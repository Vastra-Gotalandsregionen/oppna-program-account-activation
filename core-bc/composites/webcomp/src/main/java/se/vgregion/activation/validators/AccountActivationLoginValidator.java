package se.vgregion.activation.validators;

import javax.annotation.Resource;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import se.vgregion.account.services.AccountService;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationAccountStatus;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.activation.formbeans.PasswordFormBean;

public class AccountActivationLoginValidator implements Validator {
    @Resource
    private AccountService accountService;

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordFormBean form = (PasswordFormBean) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "activationCode", "code.missing",
                                                  "Activation code is missing");

        if (!errors.hasErrors()) {
            ActivationAccount account = accountService.getAccount(new ActivationCode(form.getActivationCode()));
            if (account == null) {
                errors.rejectValue("activationCode", "code.illegal", "Felaktig aktiveringskod");
            } else {
                ActivationAccountStatus status = account.currentStatus();
                if (status == ActivationAccountStatus.INVALID) {
                    errors.rejectValue("activationCode", status.toString(), "Aktiveringskoden har redan använts");
                } else if (status == ActivationAccountStatus.EXPIRED) {
                    errors.rejectValue("activationCode", status.toString(), "Aktiveringskoden är för gammal");
                }
            }
        }
    }

}
