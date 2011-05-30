package se.vgregion.activation.formbeans;

import se.vgregion.activation.domain.ActivationCode;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 26/5-11
 * Time: 12:24
 */
public class ReinviteFormBean {
    private ActivationCode activationCode;
    private String vgrId;
    private String fullName;
    private String email;
    private String organization;
    private String system;
    private String sponsor;

    public ActivationCode getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(ActivationCode activationCode) {
        this.activationCode = activationCode;
    }

    public String getVgrId() {
        return vgrId;
    }

    public void setVgrId(String vgrId) {
        this.vgrId = vgrId;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }
}
