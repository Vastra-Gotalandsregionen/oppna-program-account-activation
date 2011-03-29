package se.vgregion.activation.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_activation_account")
public class ActivationAccount extends AbstractEntity<ActivationCode> implements Serializable {

    private static Logger LOGGER = LoggerFactory.getLogger(ActivationAccount.class);

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ActivationCode activationCode;

    // @Version
    // private Long version;

    @Column(unique = true)
    private String vgrId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expire;

    private boolean invalid;

    private String customUrl;

    public ActivationAccount(String vgrId) {
        this(vgrId, "");
    }

    public ActivationAccount(String vgrId, String customUrl) {
        this.vgrId = vgrId;
        this.customUrl = customUrl;
        activationCode = ActivationCode.generate();
        activate();
    }

    public ActivationCode getId() {
        return activationCode;
    }

    public ActivationCode getActivationCode() {
        return activationCode;
    }

    public String getVgrId() {
        return vgrId;
    }

    public String getCustomUrl() {
        return customUrl;
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
        if (expire == null) {
            return null;
        }
        return new Date(expire.getTime());
    }

    public void setExpireDate(Date expire) {
        if (expire == null) {
            expire = null;
        } else {
            this.expire = new Date(expire.getTime());
        }
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
        return !invalid;
    }

    /**
     * Invalidates the account which makes it invalid to use. This action can not be reverted.
     */
    public void invalidate() {
        invalid = true;
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

    ActivationAccount() {
        // To make JPA happy
    }
}
