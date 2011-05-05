package se.vgregion.activation.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.formbeans.PasswordFormBean;

import javax.annotation.Resource;

public class ExternalUserValidator implements Validator {
    @Resource
    AccountService accountService;

    @Override
    public boolean supports(Class<?> clazz) {
        return ExternalUserFormBean.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordFormBean form = (PasswordFormBean) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.missing", "Name must be specified");

        if (!errors.hasErrors()) {
//            ActivationAccount account = accountService.getAccount(new ActivationCode(form.getActivationCode()));
//            if (account == null) {
//                errors.rejectValue("activationCode", "code.illegal", "Felaktig aktiveringskod");
//            } else {
//                ActivationAccountStatus status = account.currentStatus();
//                if (status == ActivationAccountStatus.INVALID) {
//                    errors.rejectValue("activationCode", status.toString(), "Aktiveringskoden har redan använts");
//                } else if (status == ActivationAccountStatus.EXPIRED) {
//                    errors.rejectValue("activationCode", status.toString(), "Aktiveringskoden är för gammal");
//                }
//            }
        }
    }

}
