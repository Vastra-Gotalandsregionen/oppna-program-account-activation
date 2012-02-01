package se.vgregion.activation.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.create.domain.InvitePreferences;

import java.net.MalformedURLException;
import java.net.URL;

public class InvitePreferencesValidator implements Validator {

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @Override
    public boolean supports(Class<?> clazz) {
        return InvitePreferences.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //Check unique title
        InvitePreferences invitePreferences = (InvitePreferences) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "notnull.title", "Det måste finnas en titel");
        InvitePreferences persisted = invitePreferencesService.findByTitle(invitePreferences.getTitle());
        if (persisted != null
                && (invitePreferences.getId() == null || !invitePreferences.getId().equals(persisted.getId()))) {
            errors.rejectValue("title", "duplicate.title", "Titeln måste vara unik");
        }

        // invalid url
        try {
            new URL(invitePreferences.getCustomUrl());
        } catch (MalformedURLException e) {
            errors.rejectValue("customUrl", "invalid.url", "Felaktig url");
        }
    }

}
