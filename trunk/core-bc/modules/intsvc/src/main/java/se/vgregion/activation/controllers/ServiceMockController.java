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

package se.vgregion.activation.controllers;

import static se.vgregion.activation.controllers.DTOAssembler.*;

import java.net.MalformedURLException;
import java.util.Collection;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import se.vgregion.account.services.AccountService;
import se.vgregion.activation.api.OneTimeAccountDTO;
import se.vgregion.activation.domain.OneTimePassword;
import se.vgregion.activation.domain.PublicHash;

@Path("/accounts")
@Produces("application/json")
public class ServiceMockController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMockController.class);
    @Context
    UriInfo ui;

    private AccountService accountService;

    @Autowired
    public ServiceMockController(AccountService service) {
        this.accountService = service;
    }

    @GET
    // @Secured("ROLE_RESTCLIENT")
    public Collection<OneTimeAccountDTO> getAllOneTimePassword() {
        try {
            return toDTOCollection(accountService.getAllValidAccounts(), ui);
        } catch (MalformedURLException e) {
            throw new WebApplicationException(500);
        }
    }

    @POST
    public String createOneTimePassword(OneTimeAccountDTO account) {
        try {
            return accountService.createAccount(account.getVgrId(), account.getCustomUrl().toString()).toString();
        } catch (DataAccessException e) {
            LOGGER.warn("Failed to create account.");
            throw new WebApplicationException(e, 500);
        }
    }

    @GET
    @Path("/{id}")
    public OneTimeAccountDTO getOneTimePassword(@PathParam("id") PublicHash id) {
        OneTimePassword account = accountService.getAccount(id);
        if (account == null) {
            throw new WebApplicationException(404);
        }
        try {
            return toDTO(account, ui);
        } catch (MalformedURLException e) {
            throw new WebApplicationException(500);
        }
    }

    @DELETE
    @Path("/{id}")
    public void removeOneTimePassword(@PathParam("id") PublicHash id) {
        accountService.remove(id);
    }

    @GET
    @Path("/{id}/validate")
    public Boolean validateOneTimePassword(@PathParam("id") PublicHash id) {
        OneTimePassword account = accountService.getAccount(id);
        if (account == null) {
            throw new WebApplicationException(404);
        }
        return account.isValid();
    }

    @PUT
    @Path("/{id}/reactivate")
    public void reactivateOneTimePassword(@PathParam("id") PublicHash id) {
        accountService.reactivate(id);
    }
}
