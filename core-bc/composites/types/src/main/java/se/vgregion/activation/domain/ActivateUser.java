package se.vgregion.activation.domain;

public class ActivateUser {

    public ActivateUser(String userId, String activationCode, String userPassword) {
        this.userId = userId;
        this.activationCode = activationCode;
        this.userPassword = userPassword;
    }

    private String userId;

    private String activationCode;

    private String userPassword;
}
