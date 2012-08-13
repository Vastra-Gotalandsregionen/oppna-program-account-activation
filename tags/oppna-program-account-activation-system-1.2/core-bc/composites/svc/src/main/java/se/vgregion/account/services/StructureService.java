package se.vgregion.account.services;

import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Service for searching and storing "externStructurePersonDn"s.
 * <p/>
 * User: david
 * Date: 31/5-11
 * Time: 16:51
 */
public interface StructureService {

    /**
     * Search by query.
     *
     * @param query query
     * @return An "externStructurePersonDn" from the given query.
     */
    Collection<String> search(String query);

    /**
     * Stores the given "externStructurePersonDn".
     *
     * @param externStructurePersonDn externStructurePersonDn
     */
    @Transactional
    void storeExternStructurePersonDn(String externStructurePersonDn);

    /**
     * Sets the maximum number of search results that the <code>search(java.lang.String)</code> method.
     *
     * @param maxResults Maximum number of results
     */
    void setMaxResults(int maxResults);
}
