package se.vgregion.create.domain;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * User: pabe
 * Date: 2011-05-12
 * Time: 15:34
 */
@Entity
@Table(name = "vgr_activation_invite_preferences")
public class InvitePreferences extends AbstractEntity<Long> {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String customUrl;
    private String customMessage;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
}
