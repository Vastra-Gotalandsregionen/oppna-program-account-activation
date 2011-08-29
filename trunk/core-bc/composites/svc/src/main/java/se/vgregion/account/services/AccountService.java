package se.vgregion.account.services;

import java.util.Collection;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

public interface AccountService {

    /**
     * Creates an <code>ActivationAccount</code>.
     *
     * @param vgrid         vgrid
     * @param customUrl     customUrl
     * @param customMessage customMessage
     * @param system        system
     * @return The <code>ActivationCode</code> if the created <code>ActivateAccount</code>.
     */
    ActivationCode createAccount(String vgrid, String customUrl, String customMessage, String system);

    /**
     * Get all valid accounts.
     *
     * @return All valid <code>ActivateAccount</code>s, i.e. all which has not expired and has not been used
     *         already.
     */
    Collection<ActivationAccount> getAllValidAccounts();

    /**
     * Method for fetching expired <code>ActivateAccount</code>s by specifying how old they should be.
     *
     * @param minDaysOld The least number of days that must have passed since expiration date or <code>null</code>
     *                   to count from current date.
     * @param maxDaysOld The maximum number of days that should have passed since expiration date or
     *                   <code>null</code> if no restriction is wanted.
     * @return All <code>ActivateAccount</code>s which have been expired but not used.
     */
    Collection<ActivationAccount> getExpiredUnusedAccounts(Integer minDaysOld, Integer maxDaysOld);

    /**
     * Get <code>ActivationAccount</code> by <code>ActivationCode</code>.
     *
     * @param activationCode activationCode
     * @return The <code>ActivateAccount</code> with the given activationCode.
     */
    ActivationAccount getAccount(ActivationCode activationCode);

    /**
     * Get custom url, i.e. the URL where the user can activate his/her account.
     *
     * @param activationCode activationCode
     * @return The customUrl
     */
    String getCustomUrl(ActivationCode activationCode);

    /**
     * Removes the given activationCode.
     *
     * @param activationCode activationCode
     */
    void remove(ActivationCode activationCode);

    /**
     * Sets the expiration date of the <code>ActivateAccount</code> to a future date.
     *
     * @param activationAccount activationAccount
     */
    void reactivate(ActivationAccount activationAccount);

    /**
     * Inactivates the <code>ActivateAccount</code> by setting it used.
     *
     * @param activationAccount activationAccount
     */
    void inactivate(ActivationAccount activationAccount);
}