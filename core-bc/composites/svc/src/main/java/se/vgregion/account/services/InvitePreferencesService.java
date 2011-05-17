package se.vgregion.account.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.account.services.repository.InvitePreferencesRepository;
import se.vgregion.create.domain.InvitePreferences;

import java.util.Collection;
import java.util.List;

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

    public InvitePreferences findByTitle(String title) {
        List<InvitePreferences> list = (List<InvitePreferences>) invitePreferencesRepository.findByQuery("select p from InvitePreferences p where p.title = ?1",
                new String[]{title});
        if (list.size() == 0) {
            return null;
        } else if (list.size() > 1) {
            throw new IllegalStateException("There should not exist more than one InvitePreferences with the same title.");
        } else {
            return list.get(0);
        }
    }
}
