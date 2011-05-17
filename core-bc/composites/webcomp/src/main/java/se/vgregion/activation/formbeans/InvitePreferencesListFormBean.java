package se.vgregion.activation.formbeans;

import se.vgregion.create.domain.InvitePreferences;

import java.util.List;

/**
 * User: pabe
 * Date: 2011-05-12
 * Time: 16:29
 */
public class InvitePreferencesListFormBean {

    private List<InvitePreferences> invitePreferencesList;

    private InvitePreferences currentInvitePreferences;

    public List<InvitePreferences> getInvitePreferencesList() {
        return invitePreferencesList;
    }

    public void setInvitePreferencesList(List<InvitePreferences> invitePreferencesList) {
        this.invitePreferencesList = invitePreferencesList;
    }

    public InvitePreferences getCurrentInvitePreferences() {
        return currentInvitePreferences;
    }

    public void setCurrentInvitePreferences(InvitePreferences currentInvitePreferences) {
        this.currentInvitePreferences = currentInvitePreferences;
    }
}
