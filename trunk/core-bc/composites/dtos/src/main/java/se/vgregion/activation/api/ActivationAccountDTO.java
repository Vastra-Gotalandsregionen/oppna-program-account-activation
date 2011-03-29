package se.vgregion.activation.api;

import java.io.Serializable;
import java.net.URL;

public class ActivationAccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vgrId;
    private String id;
    private URL link;
    private URL customUrl;

    public ActivationAccountDTO() {
    }

    public ActivationAccountDTO(String vgrId, String id, URL link) {
        this.vgrId = vgrId;
        this.id = id;
        this.link = link;
    }

    public String getVgrId() {
        return vgrId;
    }

    public String getId() {
        return id;
    }

    public URL getLink() {
        return link;
    }

    public void setVgrId(String vgrId) {
        this.vgrId = vgrId;
    }

    public void setId(String id) {
        this.id = id;
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

}
