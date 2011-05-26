package se.vgregion.account.services.repository;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 26/5-11
 * Time: 12:53
 */
public interface ActivationAccountRepository extends JpaRepository<ActivationAccount, ActivationCode, ActivationCode> {
}
