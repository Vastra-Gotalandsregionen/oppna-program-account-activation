package se.vgregion.activation.formbeans;

import java.io.Serializable;

//@FieldMatch(first = "password", second = "passwordCheck", message = "The password fields must match")
public class PasswordFormBean implements Serializable {
    private static final long serialVersionUID = 1234234123123L;

    // @Size(min = 6)
    private String vgrId;
    private String activationCode;
    private String dominoPassword;

    // @Size(min = 6)
    // @NotNull
    private String password;
    // @NotNull
    private String passwordCheck;

    private String loginType;

    public String getVgrId() {
        return vgrId;
    }

    public void setVgrId(String vgrId) {
        this.vgrId = vgrId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    public String getDominoPassword() {
        return dominoPassword;
    }

    public void setDominoPassword(String dominoPassword) {
        this.dominoPassword = dominoPassword;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
