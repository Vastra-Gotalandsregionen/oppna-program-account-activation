package se.vgregion.activation.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;

import se.vgregion.dao.domain.patterns.valueobject.AbstractValueObject;

public class PublicHash extends AbstractValueObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public PublicHash() {
        // To make Hibernate happy
    }

    @Column(name = "publicHash")
    private String value;

    public PublicHash(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PublicHash generate() {
        return new PublicHash(UUID.randomUUID().toString());
    }
}
