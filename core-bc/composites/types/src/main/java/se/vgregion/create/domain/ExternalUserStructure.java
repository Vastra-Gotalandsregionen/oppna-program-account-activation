package se.vgregion.create.domain;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * JPA entity.
 */
@Entity
@Table(name = "vgr_activation_external_user_structure")
public class ExternalUserStructure extends AbstractEntity<Long> implements Serializable {

    private static final long serialVersionUID = 4224372736874731907L;

    /**
     * Constructor.
     */
    public ExternalUserStructure() {

    }

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn
    private ExternalUserStructure parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExternalUserStructure getParent() {
        return parent;
    }

    public void setParent(ExternalUserStructure parent) {
        this.parent = parent;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}