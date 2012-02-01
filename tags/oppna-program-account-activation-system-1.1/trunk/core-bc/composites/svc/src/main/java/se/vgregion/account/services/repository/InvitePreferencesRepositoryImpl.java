package se.vgregion.account.services.repository;

import se.vgregion.create.domain.InvitePreferences;
import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;

/**
 * Repository for {@link InvitePreferences}.
 * <p/>
 * User: pabe
 * Date: 2011-05-16
 * Time: 10:40
 */
public class InvitePreferencesRepositoryImpl extends DefaultJpaRepository<InvitePreferences, Long>
        implements InvitePreferencesRepository {
}
