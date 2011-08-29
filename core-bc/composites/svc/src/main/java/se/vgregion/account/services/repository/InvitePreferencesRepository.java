package se.vgregion.account.services.repository;

import se.vgregion.create.domain.InvitePreferences;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;

/**
 * Repository for {@link InvitePreferences}.
 * <p/>
 * User: pabe
 * Date: 2011-05-16
 * Time: 11:40
 */
public interface InvitePreferencesRepository extends JpaRepository<InvitePreferences, Long, Long> {
}
