package se.vgregion.activation.formbeans;

import java.io.Serializable;

public class ValidationFormBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String oneTimePassword;

    private String vgrId;

    private String userPassword;

    public String getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

    public String getVgrId() {
        return vgrId;
    }

    public void setVgrId(String vgrId) {
        this.vgrId = vgrId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
