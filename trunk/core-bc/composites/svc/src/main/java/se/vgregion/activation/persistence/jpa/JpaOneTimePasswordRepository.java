package se.vgregion.activation.persistence.jpa;

import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.activation.domain.OneTimePasswordRepository;
import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;

import javax.persistence.Query;
import java.util.Date;

/**
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public class JpaOneTimePasswordRepository extends DefaultJpaRepository<OneTimePassword> implements OneTimePasswordRepository {

    @Override
    public OneTimePassword find(String publicHash) {

        String queryTemplate =
                "SELECT o FROM OneTimePassword o WHERE o.publicHash = :publicHash and o.expire > :now";

        Query query = entityManager.createQuery(queryTemplate)
                .setParameter("publicHash", publicHash)
                .setParameter("now", new Date());

        return (OneTimePassword) query.getSingleResult();
    }


}
