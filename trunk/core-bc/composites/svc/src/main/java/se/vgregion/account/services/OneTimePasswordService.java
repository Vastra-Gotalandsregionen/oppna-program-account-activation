package se.vgregion.account.services;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.dao.domain.patterns.repository.Repository;

public class OneTimePasswordService {

    private Repository<OneTimePassword, UUID> repository;

    public OneTimePasswordService() {
        // To make CGLIB happy
    }

    @Autowired
    public OneTimePasswordService(Repository<OneTimePassword, UUID> repository) {
        this.repository = repository;
    }

    @Transactional
    public OneTimePassword createAccount(String vgrid) {
        OneTimePassword account = new OneTimePassword(vgrid);
        account.setExpire(new Date());
        return repository.store(account);
    }

    public Collection<OneTimePassword> getAllAccounts() {
        return repository.findAll();
    }

    public OneTimePassword getAccount(UUID publicHash) {
        return repository.find(publicHash);
    }
}
