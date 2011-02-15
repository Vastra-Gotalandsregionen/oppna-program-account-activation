package se.vgregion.activation.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

@Entity
@Table(name = "vgr_onetime_password")
public class OneTimePassword extends AbstractEntity<UUID> implements Serializable {

    @Id
    private UUID publicHash;

    @Version
    private Long version;

    private String vgrId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expire;

    private boolean used;

    public OneTimePassword() {
        publicHash = UUID.randomUUID();
    }

    public OneTimePassword(String vgrId) {
        this();
        this.vgrId = vgrId;
    }

    public UUID getId() {
        return publicHash;
    }

    public Long getVersion() {
        return version;
    }

    public String getVgrId() {
        return vgrId;
    }

    public void setVgrId(String vgrId) {
        this.vgrId = vgrId;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isValid() {
        if (expire == null) {
            return true;
        }
        return new Date().after(expire);
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
