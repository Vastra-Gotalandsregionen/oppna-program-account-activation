package se.vgregion.account.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.util.ReflectionTestUtils;
import se.vgregion.account.services.repository.ExternalUserStructureRepository;
import se.vgregion.create.domain.ExternalUserStructure;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ContextConfiguration({ "classpath:spring/datasource-config.xml", "classpath:spring/create-account-svc.xml" })
public class StructureServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private StructureService structureService;

    @Autowired
    private ExternalUserStructureRepository repository;

    /*
    Tree structure
    - D1
        - D11
            - A111
            - B111
    - S1
        - B11
            - C111
            - N111
        - D12
            - D112
                 - E1111
                    - F11111
    - D2
    - F1
        - D13
            - E111
            - F111
    - G1
        - H11
    -I1
     */

    @Before
    public void setUp() throws Exception {
        executeSqlScript("classpath:dbSetup/test-data-structure.sql", false);
    }
    @After
    public void tearDown() throws Exception {
        executeSqlScript("classpath:dbsetup/drop-test-data-structure.sql", false);
    }

    @Test
    @DirtiesContext
    public void testFindById() throws Exception {
        ExternalUserStructure ex1 = repository.find(1L);

        assertEquals("D1", ex1.getName());
        assertNull(ex1.getParent());

        ExternalUserStructure ex2 = repository.find(2L);
        assertEquals("D11", ex2.getName());
        assertEquals(ex1, ex2.getParent());
    }

    @Test
    @DirtiesContext
    public void testFindAll() throws Exception {
        List<ExternalUserStructure> result = (List<ExternalUserStructure>)repository.findAll();

        assertEquals(20, result.size());
    }

    @Test
    @DirtiesContext
    public void testSearch_I() throws Exception {
        //when
        Collection<String> result = structureService.search("I");

        //then
        assertEquals(1, result.size());
    }
    @Test
    @DirtiesContext
    public void testSearch_G() throws Exception {
        //when
        Collection<String> result = structureService.search("G");

        //then
        assertEquals(2, result.size());
        Iterator<String> iterator = result.iterator();
        assertEquals("G1", iterator.next());
        assertEquals("G1/H11", iterator.next());


    }

    @Test
    @DirtiesContext
    public void testSearch_D() throws Exception {
        //when
        structureService.setMaxResults(11);
        Collection<String> result = structureService.search("D");

        //then
        for (String s : result) {
            System.out.println(s);
        }
        assertEquals(11, result.size());
    }

    @Test
    @DirtiesContext
    public void testSearch_maxResults() throws Exception {
        //when
        structureService.setMaxResults(9);
        Collection<String> result = structureService.search("D");

        //then
        assertEquals(9, result.size());
    }

    @Test
    @DirtiesContext
    public void testSearch_F11111() throws Exception {
        //when
        Collection<String> result = structureService.search("F11111");

        //then
        assertEquals(1, result.size());
        assertEquals("S1/D12/D112/E1111/F11111", result.iterator().next());
    }

    @Test
    @DirtiesContext
    public void testSearch_S1_B11() throws Exception {
        //when
        Collection<String> result = structureService.search("S1/B11");

        //then
        assertEquals(3, result.size());
        Iterator<String> i = result.iterator();
        assertEquals("S1/B11", i.next());
        assertEquals("S1/B11/C111", i.next());
        assertEquals("S1/B11/N111", i.next());
    }
    
    @Test
    @DirtiesContext
    public void testSearch_S1_D12_D11() throws Exception {
        //when
        Collection<String> result = structureService.search("S1/D12/D11");

        //then
        assertEquals(2, result.size());
        Iterator<String> i = result.iterator();
        assertEquals("S1/D12/D112", i.next());
        assertEquals("S1/D12/D112/E1111", i.next());
    }

    @Test
    @DirtiesContext
    public void testStoreExternStructurePersonDn() throws Exception {
        String newOrgStructure = "apa/bepa/cepa/dippa";
        structureService.storeExternStructurePersonDn(newOrgStructure);

        newOrgStructure = "apa/bepa/cepa/dippa/fippla";
        structureService.storeExternStructurePersonDn(newOrgStructure);

        Collection<String> result = structureService.search("apa");
        assertEquals(2, result.size());

        result = structureService.search("bepa");
        assertEquals(2, result.size());

        result = structureService.search("cepa");
        assertEquals(2, result.size());

        result = structureService.search("dip");
        assertEquals(2, result.size());

        result = structureService.search("fip");
        assertEquals(1, result.size());


    }
}
