package se.vgregion.activation.validators;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.create.domain.InvitePreferences;

/**
 * User: pabe
 * Date: 2011-05-17
 * Time: 11:05
 */
public class InvitePreferencesValidatorTest {

    @Mock
    private InvitePreferencesService invitePreferencesService;

    @InjectMocks
    private InvitePreferencesValidator validator;

    @Test
    public void testValidate() throws Exception {
        InvitePreferences title1Id1 = new InvitePreferences();
        ReflectionUtils.setField(InvitePreferences.class.getField("id"), title1Id1, 1l);
        title1Id1.setTitle("title1");

        InvitePreferences title1Id2 = new InvitePreferences();
        ReflectionUtils.setField(InvitePreferences.class.getField("id"), title1Id2, 2l);
        title1Id1.setTitle("title1");

        InvitePreferences title2Id1 = new InvitePreferences();
        ReflectionUtils.setField(InvitePreferences.class.getField("id"), title2Id1, 1l);
        title1Id1.setTitle("title2");

        InvitePreferences title2Id2 = new InvitePreferences();
        ReflectionUtils.setField(InvitePreferences.class.getField("id"), title2Id2, 2l);
        title1Id1.setTitle("title2");

        InvitePreferences title1IdNull = new InvitePreferences();
        title1Id1.setTitle("title1");

        Mockito.when(invitePreferencesService.findByTitle(Mockito.anyString())).thenReturn(title1Id1);
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(title1IdNull, errors);
    }
}
