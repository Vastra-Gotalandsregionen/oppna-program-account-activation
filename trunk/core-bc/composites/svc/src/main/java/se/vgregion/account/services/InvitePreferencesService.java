package se.vgregion.account.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.account.services.repository.InvitePreferencesRepository;
import se.vgregion.create.domain.InvitePreferences;

import java.util.Collection;

/**
 * User: pabe
 * Date: 2011-05-16
 * Time: 14:40
 */
public class InvitePreferencesService {

    @Autowired
    InvitePreferencesRepository invitePreferencesRepository;

    @Transactional
    public InvitePreferences merge(InvitePreferences invitePreferences) {
        return invitePreferencesRepository.merge(invitePreferences);
    }

    public Collection<InvitePreferences> findAll() {
        return invitePreferencesRepository.findAll();
    }

    public InvitePreferences find(Long id) {
        return invitePreferencesRepository.find(id);
    }

    @Transactional
    public void remove(Long id) {
        invitePreferencesRepository.remove(id);
    }
}
