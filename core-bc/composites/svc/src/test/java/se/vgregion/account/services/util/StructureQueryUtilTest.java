package se.vgregion.account.services.util;

import org.junit.Test;
import se.vgregion.account.services.util.StructureQueryUtil;
import se.vgregion.create.domain.ExternalUserStructure;

import static org.junit.Assert.assertEquals;

/**
 * User: pabe
 * Date: 2011-05-09
 * Time: 13:26
 */
public class StructureQueryUtilTest {
    StructureQueryUtil queryUtil = new StructureQueryUtil();

    @Test
    public void testWhereClause() throws Exception {
        String[] queryParts = {"apa ab"};
        String result = queryUtil.whereClause(queryParts, true);

        assertEquals("where lower(s.name) like ?1", result);

        queryParts = new String[]{"apa ab", "bepa division"};
        result = queryUtil.whereClause(queryParts, true);

        assertEquals("join s.parent s2 where lower(s2.name) = ?1 and lower(s.name) like ?2", result);

        queryParts = new String[]{"apa ab", "bepa division", "cepa unit"};
        result = queryUtil.whereClause(queryParts, true);

        assertEquals("join s.parent s2 join s.parent.parent s3 where lower(s3.name) = ?1 and lower(s2.name) = ?2 and " +
                "lower(s.name) like ?3", result);

        queryParts = new String[]{};
        result = queryUtil.whereClause(queryParts, true);

        assertEquals("", result);
        
    }

    @Test
    public void testResolveBase() throws Exception {
        ExternalUserStructure ex1 = new ExternalUserStructure();
        ex1.setName("apa");

        ExternalUserStructure ex2 = new ExternalUserStructure();
        ex2.setName("bepa");
        ex2.setParent(ex1);

        ExternalUserStructure ex3 = new ExternalUserStructure();
        ex3.setName("cepa");
        ex3.setParent(ex2);

        assertEquals("apa", queryUtil.resolveBase(ex1) );
        assertEquals("apa/bepa", queryUtil.resolveBase(ex2) );
        assertEquals("apa/bepa/cepa", queryUtil.resolveBase(ex3) );
    }
}
