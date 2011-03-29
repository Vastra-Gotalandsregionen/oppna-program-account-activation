package se.vgregion.activation.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.ws.rs.core.UriInfo;

import se.vgregion.activation.api.ActivationAccountDTO;
import se.vgregion.activation.domain.ActivationAccount;

public final class DTOAssembler {
    static ActivationAccountDTO toDTO(final ActivationAccount account, UriInfo ui) throws MalformedURLException {
        ActivationAccountDTO dto = null;
        if (account != null) {
            URL link = new URL(ui.getBaseUri() + ui.getPath() + "/" + account.getId().getValue());
            dto = new ActivationAccountDTO(account.getVgrId(), account.getActivationCode().getValue(), link);
        }
        return dto;
    }

    static Collection<ActivationAccountDTO> toDTOCollection(final Collection<ActivationAccount> allAccounts, UriInfo ui)
            throws MalformedURLException {
        Collection<ActivationAccountDTO> dtoCollection = Collections.emptySet();
        if (allAccounts != null) {
            dtoCollection = new HashSet<ActivationAccountDTO>(allAccounts.size());
            for (ActivationAccount account : allAccounts) {
                dtoCollection.add(toDTO(account, ui));
            }
        }
        return dtoCollection;
    }
}
