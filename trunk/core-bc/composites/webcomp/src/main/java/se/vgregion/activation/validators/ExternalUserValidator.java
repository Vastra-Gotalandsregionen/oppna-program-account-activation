package se.vgregion.activation.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.formbeans.PasswordFormBean;

import javax.annotation.Resource;
import java.util.regex.Pattern;

public class ExternalUserValidator implements Validator {
    @Resource
    AccountService accountService;

    private final static Pattern EMAIL_PATTERN = Pattern.compile(".+@.+\\.[a-z]+");

    @Override
    public boolean supports(Class<?> clazz) {
        return ExternalUserFormBean.class.equals(clazz);
    }

    public void validateWithLoggedInUser(Object target, Errors errors, String loggedIn) {
        validate(target, errors);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ExternalUserFormBean form = (ExternalUserFormBean) target;

        // 4: mandatory information provided

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "name.missing", "Name must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "surname.missing", "Surname must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "email.missing", "Email must be specified");

        String email = form.getEmail();
        if (!isEmail(email)) {
            errors.rejectValue("email", "invalid.email", "Invalid email");
        }

    }

    private boolean isEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }
}
