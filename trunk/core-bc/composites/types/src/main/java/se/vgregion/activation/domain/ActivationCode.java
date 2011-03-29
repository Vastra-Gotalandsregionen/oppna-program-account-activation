package se.vgregion.activation.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;

import se.vgregion.dao.domain.patterns.valueobject.AbstractValueObject;

public class ActivationCode extends AbstractValueObject implements Serializable {
    private static final long serialVersionUID = 1L;

    public ActivationCode() {
        // To make Hibernate happy
    }

    @Column(name = "activationCode")
    private String value;

    public ActivationCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActivationCode generate() {
        return new ActivationCode(UUID.randomUUID().toString());
    }
}
