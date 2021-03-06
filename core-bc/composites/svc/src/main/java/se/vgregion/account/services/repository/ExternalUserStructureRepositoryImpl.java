package se.vgregion.account.services.repository;

import se.vgregion.create.domain.ExternalUserStructure;
import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;

/**
 * Repository class for {@link ExternalUserStructure}.
 * <p/>
 * User: pabe
 * Date: 2011-05-16
 * Time: 10:40
 */
public class ExternalUserStructureRepositoryImpl extends DefaultJpaRepository<ExternalUserStructure, Long>
        implements ExternalUserStructureRepository {

}
