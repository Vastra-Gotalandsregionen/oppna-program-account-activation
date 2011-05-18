package se.vgregion.activation.controllers;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.create.domain.InvitePreferences;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 * User: pabe
 * Date: 2011-05-16
 * Time: 16:29
 */
public class InvitePreferencesPropertyEditor extends PropertyEditorSupport {

    @Autowired
    private InvitePreferencesService invitePreferencesService;

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isNotBlank(text)) {
            Long id = Long.parseLong(text);
            InvitePreferences preferences = invitePreferencesService.find(id);
            setValue(preferences);
        }
    }

    @Override
    public String getAsText() {
        InvitePreferences preferences = (InvitePreferences) getValue();
        if (preferences != null) {
            return preferences.getId().toString();
        } else {
            return "";
        }
    }

}
