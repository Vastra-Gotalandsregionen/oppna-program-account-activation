package se.vgregion.account.services.util;

import se.vgregion.create.domain.ExternalUserStructure;

/**
 * Util class for dealing with "externUserStructurePersonDn" <code>String</code>s.
 * <p/>
 * User: pabe
 * Date: 2011-05-09
 * Time: 13:23
 */
public class StructureQueryUtil {

    /**
     * Creates a where-clause from query parts.
     *
     * @param queryParts Array of names, e.g. ["company", "division"]
     * @param like Whether the query should use "like" or "=".
     * @return A where clause from the given queryParts.
     */
    public String whereClause(String[] queryParts, boolean like) {
        if (queryParts == null || queryParts.length == 0) {
            return "";
        }

        StringBuilder whereClause = new StringBuilder("where ");
        int length = queryParts.length;
        for (int i = 0; i < length - 1; i++) {
            StringBuilder joinClause = new StringBuilder("join s");
            for (int j = length - 1; j > i; j--) {
                joinClause.append(".parent");
            }
            joinClause.append(" s" + (length - i) + " ");
            whereClause.insert(0, joinClause);
            whereClause.append("lower(s" + (length - i) + ".name) = ?" + (i + 1) + " and ");
        }
        if (like) {
            whereClause.append("lower(s.name) like ?" + length);
        } else {
            whereClause.append("lower(s.name) = ?" + length);
        }

        return whereClause.toString();
    }

    /**
     * Resolves the base from a structure.
     *
     * @param structure structure
     * @return If a structure like "company/division/subdivision" exists and a structure with name "subdivision"
     * is passed as argument, then "company/division/" will be returned.
     */
    public String resolveBase(ExternalUserStructure structure) {
        ExternalUserStructure parent;
        StringBuilder sb = new StringBuilder();
        parent = structure;
        sb.append(parent.getName());
        while (parent.getParent() != null) {
            parent = parent.getParent();
            sb.insert(0, parent.getName() + "/");
        }
        //Now parent is root parent
        return sb.toString();
    }


}
