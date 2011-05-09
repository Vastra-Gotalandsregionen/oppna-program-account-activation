package se.vgregion.account.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import se.vgregion.create.domain.ExternalUserStructure;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * User: pabe
 * Date: 2011-05-09
 * Time: 14:36
 */
public class StructureServiceTest {

    @Mock
    private JpaRepository<ExternalUserStructure, Long, Long> structureRepository;

    @InjectMocks
    private StructureService structureService;

    @Before
    public void setUp() throws Exception {
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

         */
    }

    @Test
    public void testSearch() throws Exception {

        //given

        ExternalUserStructure ex1 = mock(ExternalUserStructure.class);
        ExternalUserStructure ex2 = mock(ExternalUserStructure.class);
        List<ExternalUserStructure> externalUserStructures = Arrays.asList(ex1, ex2);

        //when

        when(structureRepository.findByQuery(anyString(), any(new String[]{}.getClass()))).thenReturn(null);//todo
    }
}
