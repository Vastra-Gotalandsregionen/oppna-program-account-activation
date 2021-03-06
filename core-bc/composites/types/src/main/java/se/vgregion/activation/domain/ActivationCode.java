package se.vgregion.activation.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import se.vgregion.dao.domain.patterns.valueobject.AbstractValueObject;

@Embeddable
public class ActivationCode extends AbstractValueObject implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public ActivationCode() {
        // To make Hibernate happy
    }

    @Column(name = "activationCode")
    private String value;

    /**
     * Constructor.
     *
     * @param value value
     */
    public ActivationCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Generate an <code>ActivationCode</code> with random value.
     *
     * @return A new <code>ActivationCode</code>.
     */
    public static ActivationCode generate() {
        return new ActivationCode(UUID.randomUUID().toString());
    }
}
