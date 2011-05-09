package se.vgregion.account.services.util;

import se.vgregion.create.domain.ExternalUserStructure;

/**
 * User: pabe
 * Date: 2011-05-09
 * Time: 13:23
 */
public class StructureQueryUtil {

    public String whereClause(String[] queryParts) {
        if (queryParts == null || queryParts.length == 0) return "";

        StringBuilder whereClause = new StringBuilder("where ");
        int length = queryParts.length;
        for (int i = 0; i < length-1 ; i++) {
            String queryPart = queryParts[i];
            whereClause.append("s.");
            for (int j = length-1; j > i; j--) {
                whereClause.append("parent.");
            }
            whereClause.append("name = ?"+(i+1)+" and ");
        }
        whereClause.append("s.name like ?"+ length);

        return whereClause.toString();
    }

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
