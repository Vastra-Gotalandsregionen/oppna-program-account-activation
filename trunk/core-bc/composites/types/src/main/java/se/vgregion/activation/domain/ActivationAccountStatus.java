package se.vgregion.activation.domain;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 29/4-11
 * Time: 14:00
 */
public enum ActivationAccountStatus {
    OK, INVALID, EXPIRED;

    @Override
    public String toString() {
        return "code." + this.name().toLowerCase();
    }
}
