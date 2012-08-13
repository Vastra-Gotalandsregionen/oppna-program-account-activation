package se.vgregion.account.services.repository;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 26/5-11
 * Time: 13:10
 */
public class ActivationAccountRepositoryImpl extends DefaultJpaRepository<ActivationAccount,
        ActivationCode> implements  ActivationAccountRepository {
}
