package se.vgregion.activation.api;

import java.io.Serializable;
import java.net.URL;

public class ActivationAccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vgrId;
    private String id;
    private URL link;
    private URL customUrl;
    private String customMessage;
    private String status;
    private String system;

    /**
     * Constructor.
     */
    public ActivationAccountDTO() {
    }

    /**
     * Constructor.
     * 
     * @param vgrId vgrId
     * @param id id
     * @param link link
     * @param customUrl customUrl
     * @param customMessage customMessage
     * @param status status
     * @param system system
     */
    public ActivationAccountDTO(String vgrId, String id, URL link, URL customUrl, String customMessage,
                                String status, String system) {
        this.vgrId = vgrId;
        this.id = id;
        this.link = link;
        this.customUrl = customUrl;
        this.customMessage = customMessage;
        this.status = status;
        this.system = system;
    }

    public String getVgrId() {
        return vgrId;
    }

    public void setVgrId(String vgrId) {
        this.vgrId = vgrId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public void setCustomUrl(URL customUrl) {
        this.customUrl = customUrl;
    }

    public URL getCustomUrl() {
        return customUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
