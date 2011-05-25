package se.vgregion.create.domain;

import org.hibernate.annotations.Index;
import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * User: pabe
 * Date: 2011-05-12
 * Time: 15:34
 */
@Entity
@Table(name = "vgr_activation_invite_preferences", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
public class InvitePreferences extends AbstractEntity<Long> implements Serializable {

    private static final long serialVersionUID = 7721111513083589673L;

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
