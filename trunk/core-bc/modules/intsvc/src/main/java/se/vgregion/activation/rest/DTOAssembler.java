package se.vgregion.activation.rest;

import se.vgregion.activation.api.ActivationAccountDTO;
import se.vgregion.activation.domain.ActivationAccount;

import javax.ws.rs.core.UriInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public final class DTOAssembler {

    //Only static methods. No public constructor.
    private DTOAssembler() {

    }

    static ActivationAccountDTO toDTO(final ActivationAccount account, UriInfo ui) throws MalformedURLException {
        return toDTO(account, ui, "");
    }

    static ActivationAccountDTO toDTO(final ActivationAccount account, UriInfo ui, String pathSuffix)
            throws MalformedURLException {
        ActivationAccountDTO dto = null;
        if (account != null) {
            String base = getBaseUrl(ui);

            URL link = new URL(base + ui.getPath() + pathSuffix);
            URL customUrl = new URL(account.getCustomUrl());

            dto = new ActivationAccountDTO(account.getVgrId(), account.getActivationCode().getValue(), link, customUrl,
                    account.getCustomMessage(), account.currentStatus().name(), account.getSystem());
        }
        return dto;
    }

    static String getBaseUrl(UriInfo ui) {
        String base = ui.getBaseUri().toString();
        if (!base.endsWith("/")) {
            base += "/";
        }
        return base;
    }

    static Collection<ActivationAccountDTO> toDTOCollection(final Collection<ActivationAccount> allAccounts,
            UriInfo ui) throws MalformedURLException {
        Collection<ActivationAccountDTO> dtoCollection = Collections.emptySet();
        if (allAccounts != null) {
            dtoCollection = new HashSet<ActivationAccountDTO>(allAccounts.size());
            for (ActivationAccount account : allAccounts) {
                dtoCollection.add(toDTO(account, ui, account.getId().getValue()));
            }
        }
        return dtoCollection;
    }
}
