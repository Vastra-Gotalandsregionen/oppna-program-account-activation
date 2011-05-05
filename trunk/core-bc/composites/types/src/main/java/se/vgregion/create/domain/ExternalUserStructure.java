package se.vgregion.create.domain;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "vgr_external_user_structure")
public class ExternalUserStructure extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    String name;

    @ManyToOne(cascade = CascadeType.ALL)
    ExternalUserStructure parent;

    @Override
    public Long getId() {
        return null;
    }
}