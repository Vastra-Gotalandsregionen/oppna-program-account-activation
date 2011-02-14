package se.vgregion.activation.domain;


import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table("vgr_onetime_password")
public class OneTimePassword extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    private String publicHash;

    private String vgrId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expire;

    private boolean used;


    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public String getPublicHash() {
        return publicHash;
    }

    public void setPublicHash(String publicHash) {
        this.publicHash = publicHash;
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

    public void setUsed(boolean used) {
        this.used = used;
    }
}
