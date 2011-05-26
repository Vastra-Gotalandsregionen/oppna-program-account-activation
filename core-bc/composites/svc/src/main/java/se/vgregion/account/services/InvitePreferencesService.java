package se.vgregion.account.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.account.services.repository.InvitePreferencesRepository;
import se.vgregion.create.domain.InvitePreferences;

import java.util.Collections;
import java.util.Comparator;
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

    public List<InvitePreferences> findAll() {
        List<InvitePreferences> preferenceses = (List<InvitePreferences>) invitePreferencesRepository.findAll();
        Collections.sort(preferenceses, new Comparator<InvitePreferences>() {
            @Override
            public int compare(InvitePreferences o1, InvitePreferences o2) {
                return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
            }
        });

        return preferenceses;
    }

    public InvitePreferences find(Long id) {
        return invitePreferencesRepository.find(id);
    }

    @Transactional
    public void remove(Long id) {
        invitePreferencesRepository.remove(id);
    }

    public InvitePreferences findByTitle(String title) {
        List<InvitePreferences> list = (List<InvitePreferences>) invitePreferencesRepository.
                findByQuery("select p from InvitePreferences p where p.title = ?1", new String[]{title});
        if (list.size() == 0) {
            return null;
        } else if (list.size() > 1) {
            throw new IllegalStateException("There should not exist more than one InvitePreferences with the same title.");
        } else {
            return list.get(0);
        }
    }

    public InvitePreferences findByCustomUrl(String customUrl) {
        List<InvitePreferences> list = (List<InvitePreferences>) invitePreferencesRepository.
                findByQuery("select p from InvitePreferences p where p.customUrl = ?1", new String[]{customUrl});
        if (list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }
}
