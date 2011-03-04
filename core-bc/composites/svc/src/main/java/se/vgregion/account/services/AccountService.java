package se.vgregion.account.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import org.springframework.transaction.annotation.Transactional;

import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;

public class AccountService {

    private JpaRepository<OneTimePassword, PublicHash, PublicHash> repository;

    public AccountService() {
        // To make CGLIB happy
    }

    public AccountService(JpaRepository<OneTimePassword, PublicHash, PublicHash> repository) {
        this.repository = repository;
    }

    @Transactional
    public PublicHash createAccount(String vgrid) {
        OneTimePassword account = new OneTimePassword(vgrid);
        return repository.store(account).getPublicHash();
    }

    public Collection<OneTimePassword> getAllAccounts() {
        return Collections.unmodifiableCollection(repository.findAll());
    }

    public Collection<OneTimePassword> getAllValidAccounts() {
        return repository.findByQuery("SELECT a FROM OneTimePassword a WHERE a.used=?1 AND a.expire > ?2",
                new Object[] { Boolean.FALSE, new Date() });
    }

    public OneTimePassword getAccount(PublicHash publicHash) {
        return repository.find(publicHash);
    }

    public void removeAccount(PublicHash publicHash) {
        repository.remove(publicHash);
    }

    @Transactional
    public void reactivate(PublicHash publicHash) {
        OneTimePassword account = repository.find(publicHash);
        account.reactivate();
        repository.store(account);
    }

}
