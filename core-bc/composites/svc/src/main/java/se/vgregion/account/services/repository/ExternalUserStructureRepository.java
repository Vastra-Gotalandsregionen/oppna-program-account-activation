package se.vgregion.account.services.repository;

import se.vgregion.create.domain.ExternalUserStructure;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;

/**
 * Repository for {@link ExternalUserStructure}.
 * <p/>
 * User: pabe
 * Date: 2011-05-16
 * Time: 11:27
 */
public interface ExternalUserStructureRepository extends JpaRepository<ExternalUserStructure, Long, Long> {

}
