package se.vgregion.create.domain;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "vgr_external_user_structure")
public class ExternalUserStructure extends AbstractEntity<Long> implements Serializable {

    public ExternalUserStructure() {}

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