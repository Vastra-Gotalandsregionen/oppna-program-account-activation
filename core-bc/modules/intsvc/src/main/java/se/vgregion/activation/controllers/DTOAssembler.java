package se.vgregion.activation.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.ws.rs.core.UriInfo;

import se.vgregion.activation.api.OneTimeAccountDTO;
import se.vgregion.activation.domain.OneTimePassword;

public final class DTOAssembler {
    static OneTimeAccountDTO toDTO(final OneTimePassword account, UriInfo ui) throws MalformedURLException {
        OneTimeAccountDTO dto = null;
        if (account != null) {
            URL link = new URL(ui.getBaseUri() + ui.getPath() + "/" + account.getId().getValue());
            dto = new OneTimeAccountDTO(account.getVgrId(), account.getPublicHash().getValue(), link);
        }
        return dto;
    }

    static Collection<OneTimeAccountDTO> toDTOCollection(final Collection<OneTimePassword> allAccounts, UriInfo ui)
            throws MalformedURLException {
        Collection<OneTimeAccountDTO> dtoCollection = Collections.emptySet();
        if (allAccounts != null) {
            dtoCollection = new HashSet<OneTimeAccountDTO>(allAccounts.size());
            for (OneTimePassword account : allAccounts) {
                dtoCollection.add(toDTO(account, ui));
            }
        }
        return dtoCollection;
    }
}
