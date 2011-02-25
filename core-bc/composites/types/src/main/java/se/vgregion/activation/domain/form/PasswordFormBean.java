package se.vgregion.activation.domain.form;

import org.hibernate.validator.constraints.NotEmpty;
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.activation.domain.validation.CheckPassword;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@CheckPassword
public class PasswordFormBean implements Serializable {
    private static final long serialVersionUID = 1234234123123L;

    private String vgrId;
    private String oneTimePassword;
    private String dominoPassword;

    private String password;
    private String passwordCheck;

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

    public String getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(String oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }
}
