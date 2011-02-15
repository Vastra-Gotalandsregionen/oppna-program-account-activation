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

import java.util.Collection;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import se.vgregion.account.services.OneTimePasswordService;
import se.vgregion.activation.domain.OneTimePassword;

@Controller
@RequestMapping("/accounts")
public class ServiceMockController {

    private OneTimePasswordService accountService;

    @Autowired
    public ServiceMockController(OneTimePasswordService service) {
        this.accountService = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    Collection<OneTimePassword> getAllOneTimePassword() {
        return accountService.getAllAccounts();
    }

    @RequestMapping(method = RequestMethod.POST, headers = "content-type=application/json")
    public OneTimePassword createOneTimePassword(@RequestBody OneTimeAccountDTO account) {
        return accountService.createAccount(account.getVgrId());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    OneTimePassword getOneTimePassword(@PathVariable("id") UUID id) {
        return accountService.getAccount(id);
    }

    // @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    // public void removeOneTimePassword(@PathVariable("id") UUID id) {
    // accountService.removeAccount(id);
    // }

    @RequestMapping(value = "/{id}/valid", method = RequestMethod.GET)
    public @ResponseBody
    Boolean validateOneTimePassword(@PathVariable("id") UUID id) {
        OneTimePassword password = accountService.getAccount(id);
        return password.isValid();
    }

    // @RequestMapping(value = "/{id}/reactivate", method = RequestMethod.PUT)
    // public @ResponseBody
    // void reactivateOneTimePassword(@PathVariable("id") UUID id) {
    // repository.reactivate(id);
    // }

}
