package se.vgregion.activation.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.vgregion.activation.formbeans.ExternalUserFormBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ExternalUserValidator implements Validator {

    private final static Pattern EMAIL_PATTERN = Pattern.compile(".+@.+\\.[a-z]+");

    @Override
    public boolean supports(Class<?> clazz) {
        return ExternalUserFormBean.class.equals(clazz);
    }

    public void validateWithLoggedInUser(ExternalUserFormBean form, Errors errors, String loggedInUser) {
        if (validateLoggedInUser(form, errors, loggedInUser)) return;

        // mandatory information provided

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "invitePreferences", "invalid.invitePreferences.missing",
                "Invite must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "invalid.name.missing", "Name must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "surname", "invalid.surname.missing", "Surname must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "invalid.email.missing", "Email must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "externStructurePersonDn",
                "invalid.externStructurePersonDn.missing", "Organisation must be specified");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateLimit",
                "invalid.dateLimit.missing", "Date limit must be specified");

        String email = form.getEmail();
        if (!isEmail(email)) {
            errors.rejectValue("email", "invalid.email.format", "Invalid email");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date chosenDate = sdf.parse(form.getDateLimit());
            if (chosenDate.before(new Date())) {
                errors.rejectValue("dateLimit", "invalid.dateLimit.oldDate", "The date must not be old");
            }
            //In case it was possible to parse only because the parser was forgiving we check that it actually is the right date
            if (!sdf.format(chosenDate).equals(form.getDateLimit())) {
                errors.rejectValue("dateLimit", "invalid.dateLimit", "Invalid date");
            }
        } catch (ParseException e) {
            errors.rejectValue("dateLimit", "invalid.dateLimit.format", "Invalid date format");
        }

    }

    private boolean validateLoggedInUser(ExternalUserFormBean form, Errors errors, String loggedInUser) {
        // loggedInUser
        if (form.getSponsorVgrId() == null) {
            errors.rejectValue("sponsorVgrId", "invalid.sponsorVgrId.empty", "No sponsor given");
            return true;
        }

        if (!form.getSponsorVgrId().equals(loggedInUser)) {
            errors.rejectValue("sponsorVgrId", "invalid.sponsorVgrId.mismatch", "Sponsor must be logged in");
            return true;
        }

        if (form.getSponsorVgrId().startsWith("ex_")) {
            errors.rejectValue("sponsorVgrId", "invalid.sponsorVgrId.type", "Invalid sponsor");
            return true;
        }
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
    }

    private boolean isEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }
}
