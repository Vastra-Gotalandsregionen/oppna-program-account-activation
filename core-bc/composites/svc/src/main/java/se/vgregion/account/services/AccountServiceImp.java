package se.vgregion.account.services;

import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;

@Service
public class AccountServiceImp implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImp.class);
    private final JpaRepository<ActivationAccount, ActivationCode, ActivationCode> repository;

    @Autowired
    public AccountServiceImp(JpaRepository<ActivationAccount, ActivationCode, ActivationCode> repository) {
        this.repository = repository;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#createAccount(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ActivationCode createAccount(String vgrid, String customUrl) {
        ActivationAccount account = new ActivationAccount(vgrid, customUrl);
        account = repository.store(account);
        return account.getActivationCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#getAllValidAccounts()
     */
    @Override
    public Collection<ActivationAccount> getAllValidAccounts() {
        return repository.findByQuery("SELECT a FROM ActivationAccount a WHERE a.invalid=?1 AND a.expire > ?2",
                new Object[] { Boolean.FALSE, new Date() });
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#getAccount(se.vgregion.activation.domain.ActivationCode)
     */
    @Override
    public ActivationAccount getAccount(ActivationCode activationCode) {
        return repository.find(activationCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#getCustomUrl(se.vgregion.activation.domain.ActivationCode)
     */
    @Override
    public String getCustomUrl(ActivationCode activationCode) {
        return getAccount(activationCode).getCustomUrl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#remove(se.vgregion.activation.domain.ActivationCode)
     */
    @Override
    @Transactional
    public void remove(ActivationCode activationCode) {
        repository.remove(activationCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#reactivate(se.vgregion.activation.domain.ActivationCode)
     */
    @Override
    @Transactional
    public void reactivate(ActivationCode activationCode) {
        ActivationAccount account = repository.find(activationCode);
        account.reactivate();
        repository.store(account);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#invalidate(se.vgregion.activation.domain.ActivationCode)
     */
    @Override
    @Transactional
    public void invalidate(ActivationCode activationCode) {
        ActivationAccount account = repository.find(activationCode);
        account.invalidate();
        repository.store(account);
    }

}
