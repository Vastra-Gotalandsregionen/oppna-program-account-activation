/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.activation.rest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.api.ActivationAccountDTO;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationAccountStatus;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.portal.inviteuser.InviteUser;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.MalformedURLException;
import java.util.Collection;

import static se.vgregion.activation.rest.DTOAssembler.toDTO;
import static se.vgregion.activation.rest.DTOAssembler.toDTOCollection;

@Path("/activation-codes")
@Produces("application/json")
public class ActivationCodeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivationCodeService.class);

    @Context
    private UriInfo uriInfo;

    @Value("${pathToActivation}")
    private String pathToActivation;

    private AccountService accountService = null;

    /**
     * Default constructor.
     */
    public ActivationCodeService() {
    }

    /**
     * Constructor.
     *
     * @param service service
     */
    @Autowired
    public ActivationCodeService(AccountService service) {
        this.accountService = service;
    }

    /**
     * Get all <code>ActivationAccountDTO</code>s.
     * @return All valid <code>ActivationAccount</code>s converted to <code>ActivationAccountDTO</code>s.
     */
    @GET
    public Collection<ActivationAccountDTO> getAllActivationCode() {
        try {
            return toDTOCollection(accountService.getAllValidAccounts(), uriInfo);
        } catch (MalformedURLException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Called with a JSON post. Creates an <code>ActivationAccount</code>.
     * @param account account
     * @return The <code>ActivationCode</code> of the created <code>ActivationAccount</code>.
     */
    @POST
    @Consumes("application/json")
    public ActivationCode createActivationCodeJson(ActivationAccountDTO account) {
        try {
            return accountService.createAccount(account.getVgrId(), account.getCustomUrl().toString(),
                    account.getCustomMessage(), account.getSystem());
        } catch (DataAccessException e) {
            LOGGER.warn("Failed to create account.");
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Called with an XML post. Creates an <code>ActivationAccount</code>.
     * @param inviteUser inviteUser
     * @return The <code>ActivationCode</code> of the created <code>ActivationAccount</code>.
     */
    @POST
    @Consumes("application/xml")
    public ActivationCode createActivationCodeXml(InviteUser inviteUser) {
        try {
            return accountService.createAccount(inviteUser.getUserId(), inviteUser.getCustomURL(),
                    inviteUser.getCustomMessage(), inviteUser.getSystem());
        } catch (DataAccessException e) {
            LOGGER.warn("Failed to create account.");
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get an <code>ActivationAccountDTO</code> by id.
     * @param id id
     * @return The <code>ActivationAccountDTO</code> with id "id".
     */
    @GET
    @Path("/{id}")
    public ActivationAccountDTO getActivationCode(@PathParam("id") ActivationCode id) {
        ActivationAccount account = accountService.getAccount(id);
        if (account == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        try {
            return toDTO(account, uriInfo);
        } catch (MalformedURLException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get an <code>ActivationAccountDTO</code> by vgr id.
     * @param vgrId vgrId
     * @return The <code>ActivationAccountDTO</code> with vgr id "vgrId".
     */
    @GET
    @Path("/vgrid/{vgrid}")
    public ActivationAccountDTO getActivationCode(@PathParam("vgrid") String vgrId) {
        if (StringUtils.isBlank(vgrId)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        try {
            for (ActivationAccount account : accountService.getAllValidAccounts()) {
                if (vgrId.equals(account.getVgrId())) {
                    return toDTO(account, uriInfo);
                }
            }
        } catch (MalformedURLException e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    /**
     * Inactivates an <code>ActivationAccount</code> by putting an <code>ActivationCode</code>.
     * @param id id
     */
    @PUT
    @Path("/{id}/inactivate")
    public void inactivateActivationCode(@PathParam("id") ActivationCode id) {
        ActivationAccount account = accountService.getAccount(id);
        if (account == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        accountService.inactivate(account);
    }

    /**
     * Reactivates an <code>ActivationAccount</code> by setting its expire date to a future date.
     * @param id id
     */
    @PUT
    @Path("/{id}/reactivate")
    public void reactivateActivationCode(@PathParam("id") ActivationCode id) {
        ActivationAccount account = accountService.getAccount(id);
        if (account == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        accountService.reactivate(account);
    }

    /**
     * Get the URL where the user may continue the process of activating his/her account.
     * @param id id
     * @return The URL where the user may continue the process of activating his/her account.
     */
    @GET
    @Path("/path/activation-code/{id}")
    @Produces("text/plain")
    public String getActivationFormUrl(@PathParam("id") ActivationCode id) {
        ActivationAccount account = accountService.getAccount(id);
        if (account == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (account.currentStatus() != ActivationAccountStatus.OK) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        return pathToActivation + "&activationCode=" + id.getValue();
    }

}
