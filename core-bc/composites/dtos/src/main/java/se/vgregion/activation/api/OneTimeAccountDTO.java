package se.vgregion.activation.api;

import java.io.Serializable;
import java.net.URL;

public class OneTimeAccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vgrId;
    private String id;
    private URL link;

    public OneTimeAccountDTO(String vgrId, String id, URL link) {
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

}
