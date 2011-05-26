package se.vgregion.account.services;

import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.account.services.repository.ActivationAccountRepository;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final ActivationAccountRepository repository;

    @Autowired
    public AccountServiceImpl(ActivationAccountRepository repository) {
        this.repository = repository;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.account.services.AccountService#createAccount(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ActivationCode createAccount(String vgrid, String customUrl, String customMessage) {
        ActivationAccount account = new ActivationAccount(vgrid, customUrl, customMessage);
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
        return repository.findByQuery("SELECT a FROM ActivationAccount a WHERE a.used=?1 AND a.expire > ?2",
                new Object[]{Boolean.FALSE, new Date()});
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
    public void reactivate(ActivationAccount account) {
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
    public void inactivate(ActivationAccount account) {
        account.inactivate();
        repository.store(account);
    }

}
