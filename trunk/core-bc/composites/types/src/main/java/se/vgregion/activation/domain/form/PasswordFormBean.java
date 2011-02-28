package se.vgregion.activation.domain.form;

import org.hibernate.validator.constraints.NotEmpty;
import se.vgregion.activation.domain.PublicHash;
import se.vgregion.activation.domain.validation.CheckPassword;
import se.vgregion.activation.domain.validation.FieldMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@FieldMatch(first = "password", second = "passwordCheck", message = "The password fields must match")
public class PasswordFormBean implements Serializable {
    private static final long serialVersionUID = 1234234123123L;

//    @Size(min = 6)
    private String vgrId;
    private String oneTimePassword;
    private String dominoPassword;

//    @NotNull
//    @Size(min = 6)
    private String password;
//    @NotNull
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
