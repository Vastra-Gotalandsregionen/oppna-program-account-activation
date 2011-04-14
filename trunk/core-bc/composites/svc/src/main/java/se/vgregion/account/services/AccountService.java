package se.vgregion.account.services;

import java.util.Collection;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

public interface AccountService {

    ActivationCode createAccount(String vgrid, String customUrl);

    Collection<ActivationAccount> getAllValidAccounts();

    ActivationAccount getAccount(ActivationCode activationCode);

    String getCustomUrl(ActivationCode activationCode);

    void remove(ActivationCode activationCode);

    void reactivate(ActivationAccount activationAccount);

    void inactivate(ActivationAccount activationAccount);
}