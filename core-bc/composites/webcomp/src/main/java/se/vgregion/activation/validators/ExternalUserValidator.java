package se.vgregion.activation.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.formbeans.ExternalUserFormBean;

import javax.annotation.Resource;
import java.util.regex.Pattern;

public class ExternalUserValidator implements Validator {

    private final static Pattern EMAIL_PATTERN = Pattern.compile(".+@.+\\.[a-z]+");

    @Override
    public boolean supports(Class<?> clazz) {
        return ExternalUserFormBean.class.equals(clazz);
    }

    public void validateWithLoggedInUser(ExternalUserFormBean form, Errors errors, String loggedInUser) {
        // 1: loggedInUser

        if (form.getSponsorVgrId() == null) {
            errors.rejectValue("sponsorVgrId", "invalid.sponsorVgrId.empty", "No sponsor given");
            return;
        }

        if (!form.getSponsorVgrId().equals(loggedInUser)) {
            errors.rejectValue("sponsorVgrId", "invalid.sponsorVgrId.mismatch", "Sponsor must be logged in");
            return;
        }

        if (form.getSponsorVgrId().startsWith("ex_")) {
            errors.rejectValue("sponsorVgrId", "invalid.sponsorVgrId.type", "Invalid sponsor");
            return;
        }

        validate(form, errors);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ExternalUserFormBean form = (ExternalUserFormBean) target;

        // 4: mandatory information provided

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "invalid.name.missing", "Name must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "invalid.surname.missing", "Surname must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "invalid.email.missing", "Email must be specified");

        String email = form.getEmail();
        if (!isEmail(email)) {
            errors.rejectValue("email", "invalid.email.format", "Invalid email");
        }
    }

    private boolean isEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }
}
