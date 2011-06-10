package se.vgregion.account.services;

import java.util.Collection;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

public interface AccountService {

    ActivationCode createAccount(String vgrid, String customUrl, String customMessage);

    Collection<ActivationAccount> getAllValidAccounts();

    /**
     * Method for fetching expired <code>ActivateAccount</code>s by specifying how old they should be.
     *
     * @param minDaysOld for the least number of days that must have passed since expiration date or <code>null</code>
     * to count from current date
     * @param maxDaysOld for the maximum number of days that should have passed since expiration date or
     * <code>null</code> if no restriction is wanted
     * @return
     */
    Collection<ActivationAccount> getExpiredUnusedAccounts(Integer minDaysOld, Integer maxDaysOld);

    ActivationAccount getAccount(ActivationCode activationCode);

    String getCustomUrl(ActivationCode activationCode);

    void remove(ActivationCode activationCode);

    void reactivate(ActivationAccount activationAccount);

    void inactivate(ActivationAccount activationAccount);
}