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

    private boolean used;

    private String customUrl;

    private String customMessage;

    public ActivationAccount(String vgrId) {
        this(vgrId, "", "");
    }

    public ActivationAccount(String vgrId, String customUrl, String customMessage) {
        this.vgrId = vgrId;
        this.customUrl = customUrl;
        this.customMessage = customMessage;
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

    public String getCustomMessage() {
        return customMessage;
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
     * Verifies if the account is valid or not.
     * 
     * @return false if the account has been invalidated, true otherwise.
     */
    public boolean isUsed() {
        return used;
    }

    public ActivationAccountStatus currentStatus() {
        if (isUsed()) return ActivationAccountStatus.INVALID;

        if (hasExpired()) return ActivationAccountStatus.EXPIRED;

        return ActivationAccountStatus.OK;
    }

    /**
     * Invalidates the account which makes it invalid to use. This action can not be reverted.
     */
    public void inactivate() {
        used = true;
    }

    /**
     * If account is inactive, reactivates the account by reseting the date to expire in one week from now.
     */
    public void reactivate() {
        if (isUsed()) {
            LOGGER.debug("Account has been used and can not be reactivated");
        } else if (hasExpired()) {
            activate();
        } else {
            LOGGER.debug("Account is already active and can not be reactivated");
        }
    }

    private void activate() {
        Calendar nextWeek = Calendar.getInstance();
        nextWeek.add(Calendar.WEEK_OF_YEAR, 1);
        expire = nextWeek.getTime();
    }

    protected ActivationAccount() {
        // To make JPA happy
    }
}
