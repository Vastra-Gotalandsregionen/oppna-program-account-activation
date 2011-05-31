package se.vgregion.account.services;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 31/5-11
 * Time: 16:51
 */
public interface StructureService {
    Collection<String> search(String query);

    @Transactional
    void storeExternStructurePersonDn(String externStructurePersonDn);

    void setMaxResults(int maxResults);
}
