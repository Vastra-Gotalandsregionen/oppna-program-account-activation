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
        String result = queryUtil.whereClause(queryParts);

        assertEquals("where s.name like ?1", result);

        queryParts = new String[]{"apa ab", "bepa division"};
        result = queryUtil.whereClause(queryParts);

        assertEquals("where s.parent.name = ?1 and s.name like ?2", result);

        queryParts = new String[]{"apa ab", "bepa division", "cepa unit"};
        result = queryUtil.whereClause(queryParts);

        assertEquals("where s.parent.parent.name = ?1 and s.parent.name = ?2 and s.name like ?3", result);

        queryParts = new String[]{};
        result = queryUtil.whereClause(queryParts);

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
