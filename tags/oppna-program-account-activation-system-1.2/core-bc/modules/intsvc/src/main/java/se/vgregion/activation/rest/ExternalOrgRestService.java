package se.vgregion.activation.rest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import se.vgregion.account.services.StructureService;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Service that exposes the methods of <code>StructureService</code> in a RESTful way.
 */
@Path("/")
@Produces("application/json")
public class ExternalOrgRestService {

    private StructureService structureService;

    /**
     * Constructor.
     *
     * @param structureService structureService
     */
    @Autowired
    public ExternalOrgRestService(StructureService structureService) {
        this.structureService = structureService;
    }

    /**
     * Searches for external organisation structures by query with GET request.
     * @param query query
     * @return Collection of structures in JSON format.
     */
    @GET
    @Path("/search")
    public Collection<String> search(@QueryParam("query") String query) {
        try {
            return structureService.search(query);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a given structure.
     * @param structure E.g. "company/division/subdivision"
     */
    @PUT
    @Path("/save")
    public void save(@QueryParam("organization") String structure) {
        try {
            if (StringUtils.isBlank(structure)) {
                throw new Exception("Nothing to save");
            }

            structureService.storeExternStructurePersonDn(structure);
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
