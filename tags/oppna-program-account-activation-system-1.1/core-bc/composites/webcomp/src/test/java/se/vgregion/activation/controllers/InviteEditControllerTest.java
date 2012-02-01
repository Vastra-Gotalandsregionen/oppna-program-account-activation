package se.vgregion.activation.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.activation.formbeans.InvitePreferencesFormBean;
import se.vgregion.activation.validators.InvitePreferencesValidator;
import se.vgregion.create.domain.InvitePreferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import static org.mockito.Mockito.*;

/**
 * User: pabe
 * Date: 2011-08-30
 * Time: 15:25
 */
@RunWith(MockitoJUnitRunner.class)
public class InviteEditControllerTest {

    @Mock
    InvitePreferencesService invitePreferencesService;
    @Mock
    InvitePreferencesValidator validator;

    @InjectMocks
    InviteEditController inviteEditController = new InviteEditController();

    @Test
    public void testSavePreferences() throws Exception {

        final long id = 1234L;

        //setup object
        InvitePreferencesFormBean invitePreferencesFormBean = new InvitePreferencesFormBean();//mock(InvitePreferencesFormBean.class);
        invitePreferencesFormBean.setCustomMessage("message");
        invitePreferencesFormBean.setId(id);
        invitePreferencesFormBean.setTitle("title");

        //object to verify later
        InvitePreferences invitePreferences = mock(InvitePreferences.class);
        when(invitePreferencesService.find(id)).thenReturn(invitePreferences);

        //go
        inviteEditController.savePreferences(mock(ActionRequest.class), mock(ActionResponse.class),
                invitePreferencesFormBean, mock(BindingResult.class), mock(Model.class));

        //verify
        verify(invitePreferences).setTitle("title");
        verify(invitePreferences).setCustomMessage("message");
        verify(invitePreferences).setCustomUrl(null);

        verify(invitePreferencesService).merge(invitePreferences);
    }
}
