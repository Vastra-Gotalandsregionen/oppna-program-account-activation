package se.vgregion.activation.domain;

import se.vgregion.dao.domain.patterns.repository.Repository;
import sun.awt.RepaintArea;

/**
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public interface OneTimePasswordRepository extends Repository<OneTimePassword, Long> {

    /**
     * Find stored one-time password from it's public hash.
     *
     * @param publicHash - public hash
     * @return a valid one-time password or throw exception if none found.
     */
    public OneTimePassword find(String publicHash);



}
