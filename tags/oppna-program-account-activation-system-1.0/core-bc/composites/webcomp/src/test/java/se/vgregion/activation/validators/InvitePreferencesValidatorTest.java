package se.vgregion.activation.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.Errors;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.create.domain.InvitePreferences;

import java.lang.reflect.Field;

import static org.mockito.Matchers.anyString;

/**
 * User: pabe
 * Date: 2011-05-17
 * Time: 11:05
 */
@RunWith(MockitoJUnitRunner.class)
public class InvitePreferencesValidatorTest {

    @Mock
    private InvitePreferencesService invitePreferencesService;

    @InjectMocks
    private InvitePreferencesValidator validator = new InvitePreferencesValidator();
    private InvitePreferences title1IdNull;
    private InvitePreferences title3IdNull;
    private InvitePreferences title2Id2;
    private InvitePreferences title2Id1;
    private InvitePreferences title1Id2;
    private InvitePreferences title1Id1;
    private InvitePreferences badUrl1;
    private InvitePreferences badUrl2;
    private InvitePreferences goodDodgyUrl;
    private InvitePreferences noTitleIdNull;

    @Before
    public void testValidate() throws Exception {
        title1Id1 = new InvitePreferences();
        Field field = InvitePreferences.class.getDeclaredField("id");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, title1Id1, 1l);
        title1Id1.setTitle("title1");
        title1Id1.setCustomUrl("http://example.com");

        title1Id2 = new InvitePreferences();
        ReflectionUtils.setField(field, title1Id2, 2l);
        title1Id2.setTitle("title1");
        title1Id2.setCustomUrl("http://example.com");

        title2Id1 = new InvitePreferences();
        ReflectionUtils.setField(field, title2Id1, 1l);
        title2Id1.setTitle("title2");
        title2Id1.setCustomUrl("http://example.com");

        title2Id2 = new InvitePreferences();
        ReflectionUtils.setField(field, title2Id2, 2l);
        title2Id2.setTitle("title2");
        title2Id2.setCustomUrl("http://example.com");

        title1IdNull = new InvitePreferences();
        title1IdNull.setTitle("title1");
        title1IdNull.setCustomUrl("http://example.com");

        noTitleIdNull = new InvitePreferences();
        noTitleIdNull.setTitle("");
        noTitleIdNull.setCustomUrl("http://example.com");

        title3IdNull = new InvitePreferences();
        title3IdNull.setTitle("title3");
        title3IdNull.setCustomUrl("http://example.com");

        badUrl1 = new InvitePreferences();
        badUrl1.setTitle("title3");
        badUrl1.setCustomUrl("bad");

        badUrl2 = new InvitePreferences();
        badUrl2.setTitle("title3");
        badUrl2.setCustomUrl("ttp://example.com");

        goodDodgyUrl = new InvitePreferences();
        goodDodgyUrl.setTitle("title3");
        goodDodgyUrl.setCustomUrl("http:example.com");

        Mockito.when(invitePreferencesService.findByTitle("title1")).thenReturn(title1Id1);
        Mockito.when(invitePreferencesService.findByTitle("title2")).thenReturn(title2Id2);
        Mockito.when(invitePreferencesService.findByTitle("title3")).thenReturn(null);
    }

    @Test
    public void saveExistingAsIs() {
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(title1Id1, errors);
        Mockito.verify(errors, Mockito.never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void newWithUniqueTitle() {
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(title3IdNull, errors);
        Mockito.verify(errors, Mockito.never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void changeToAlreadyExistingTitle() {
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(title1Id2, errors);
        Mockito.verify(errors).rejectValue("title", "duplicate.title", "Titeln måste vara unik");
    }

    @Test
    public void changeTitleOnExisting() {
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(title1Id1, errors);
        Mockito.verify(errors, Mockito.never()).rejectValue(anyString(), anyString(), anyString());
    }

    @Test
    public void newWithSameTitle() {
        //case 1 (a new with same title)
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(title1IdNull, errors);
        Mockito.verify(errors).rejectValue("title", "duplicate.title", "Titeln måste vara unik");
    }

    @Test
    public void noTitle() {
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(noTitleIdNull, errors);
        Mockito.verify(errors).rejectValue("title", "notnull.title", null, "Det måste finnas en titel");
    }

    @Test
    public void badUrl1() {
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(badUrl1, errors);
        Mockito.verify(errors).rejectValue("customUrl", "invalid.url", "Felaktig url");
    }
    @Test
    public void badUrl2() {
        Errors errors = Mockito.mock(Errors.class);
        validator.validate(badUrl2, errors);
        Mockito.verify(errors).rejectValue("customUrl", "invalid.url", "Felaktig url");
    }
    @Test
    public void goodDodgyUrl() {
        Errors errors =  Mockito.mock(Errors.class);
        validator.validate(goodDodgyUrl, errors);
        Mockito.verify(errors, Mockito.never()).rejectValue(anyString(), anyString(), anyString());
    }
}
