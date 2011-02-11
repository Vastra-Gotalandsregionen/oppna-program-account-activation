package model;

public class ActivationValidationResponse {
    private final boolean valid;
    private final String vgrId;
    private final String errorReason;

    public ActivationValidationResponse(boolean valid, String vgrId, String errorReason) {
        this.valid = valid;
        this.vgrId = vgrId;
        this.errorReason = errorReason;
    }

    public boolean isValid() {
        return valid;
    }

    public String getVgrId() {
        return vgrId;
    }

    public String getErrorReason() {
        return errorReason;
    }
}
