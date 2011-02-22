package se.vgregion.activation.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_onetime_password")
public class OneTimePassword extends AbstractEntity<PublicHash> implements Serializable {

    private static Logger LOGGER = LoggerFactory.getLogger(OneTimePassword.class);

    private static final long serialVersionUID = 1L;

    // private Long primaryKey;

    @Id
    private PublicHash publicHash;

    // @Version
    // private Long version;

    private String vgrId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expire;

    private boolean used;

    public OneTimePassword() {
        publicHash = PublicHash.generate();
        activate();
    }

    public OneTimePassword(String vgrId) {
        this();
        this.vgrId = vgrId;
    }

    public PublicHash getId() {
        return publicHash;
    }

    public PublicHash getPublicHash() {
        return publicHash;
    }

    public String getVgrId() {
        return vgrId;
    }

    /**
     * Verifies if the account as expired.
     * 
     * @return false if the account has expired, true otherwise.
     */
    public boolean hasExpired() {
        if (expire == null) {
            return true;
        }
        return new Date().after(expire);
    }

    public Date getExpireDate() {
        return new Date(expire.getTime());
    }

    /**
     * Verifies if the account is active or not.
     * 
     * @return false if the account has been invalidated or has expired, true otherwise.
     */
    public boolean isActive() {
        return !(isValid() || hasExpired());
    }

    /**
     * Verifies if the account is valid or not.
     * 
     * @return false if the account has been invalidated, true otherwise.
     */
    public boolean isValid() {
        return !used;
    }

    /**
     * Invalidates the account which makes it invalid to use. This action can not be reverted.
     */
    public void invalidate() {
        used = true;
    }

    /**
     * If account is inactive, reactivates the account by reseting the date to expire with one week from now.
     */
    public void reactivate() {
        if (isActive()) {
            if (isValid()) {
                LOGGER.debug("Account is already active and can not be reactivated");
            } else {
                LOGGER.debug("Account has been used and can not be reactivated");
            }
        } else {
            activate();
        }
    }

    private void activate() {
        Calendar nextWeek = Calendar.getInstance();
        nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
        expire = nextWeek.getTime();
    }
}
