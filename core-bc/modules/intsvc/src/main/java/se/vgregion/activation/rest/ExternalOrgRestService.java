package se.vgregion.activation.rest;

import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.account.services.StructureService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;

@Path("/")
@Produces("application/json")
public class ExternalOrgRestService {

    @Context
    private UriInfo uriInfo;

    private StructureService structureService;

    @Autowired
    public ExternalOrgRestService(StructureService structureService) {
        this.structureService = structureService;
    }

    @GET
    public String hello() {
        return "Hello [" + new Date() + "]";
    }

    @GET
    @Path("/search")
    public Collection<String> search(@QueryParam("query") String query) {
        try {
            return structureService.search(query);
        } catch (Exception e) {
            throw new WebApplicationException(500);
        }
    }
}
