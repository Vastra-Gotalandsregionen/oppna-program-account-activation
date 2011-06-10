package se.vgregion.activation.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Date;

@Path("/")
@Produces("application/json")
public class ExternalOrgRestService {

    @GET
    public String hello() {
        return "Hello ["+new Date()+"]";
    }
}
