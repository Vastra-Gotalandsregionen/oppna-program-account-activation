package se.vgregion.activation.formbeans;

/**
 * Class that is used to map to GUI forms.
 * <p/>
 * User: pabe
 * Date: 2011-05-12
 * Time: 16:29
 */
public class InvitePreferencesFormBean {

    private Long id;
    private String title;
    private String customMessage;
    private String customUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }
}
