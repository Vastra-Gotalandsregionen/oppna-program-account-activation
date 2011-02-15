package se.vgregion.activation.domain.form;

import java.io.Serializable;

public class ValidationFormBean implements Serializable {
    private String onetimeToken;

    private String userIdentifier;

    private String userPassword;

    public String getOnetimeToken() {
        return onetimeToken;
    }

    public void setOnetimeToken(String onetimeToken) {
        this.onetimeToken = onetimeToken;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
