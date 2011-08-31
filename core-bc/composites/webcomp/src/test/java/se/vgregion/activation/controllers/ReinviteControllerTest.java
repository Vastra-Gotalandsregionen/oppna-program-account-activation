package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.sender.MessageSender;
import com.liferay.portal.kernel.messaging.sender.SynchronousMessageSender;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import se.vgregion.account.services.AccountService;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.activation.formbeans.ReinviteFormBean;
import se.vgregion.create.domain.InvitePreferences;
import se.vgregion.ldapservice.LdapService;
import se.vgregion.ldapservice.SimpleLdapUser;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * User: pabe
 * Date: 2011-05-31
 * Time: 08:43
 */
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(MessageBusUtil.class)
public class ReinviteControllerTest {

    @Mock
    private AccountService accountService;
    @Mock
    private InvitePreferencesService invitePreferencesService;
    @Mock
    private LdapService ldapService;

    @InjectMocks
    private ReinviteController reinviteController = new ReinviteController();

    @Before
    public void setup() {
        PowerMockito.mockStatic(MessageBusUtil.class);
        MessageBusUtil.init(mock(MessageBus.class), mock(MessageSender.class), mock(SynchronousMessageSender.class));
    }

    @Test
    public void testView() {

        ArrayList<ActivationAccount> validAccounts = new ArrayList<ActivationAccount>();
        validAccounts.add(new ActivationAccount("vgrid"));
        when(accountService.getAllValidAccounts()).thenReturn(validAccounts);

        ArrayList<ActivationAccount> expiredAccounts = new ArrayList<ActivationAccount>();
        expiredAccounts.add(new ActivationAccount("expiredVgrId"));
        when(accountService.getExpiredUnusedAccounts(anyInt(), anyInt())).thenReturn(expiredAccounts);

        PortletRequest portletRequest = mock(PortletRequest.class);
        HashMap<String, String> userInfo = new HashMap<String, String>();
        userInfo.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), "loggedInUserId");
        when(portletRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfo);

        Model model = mock(Model.class);

        //go
        reinviteController.view(model, portletRequest);

        //verify
        verify(model).addAttribute(eq("accounts"), anyCollectionOf(ReinviteFormBean.class));
        verify(model).addAttribute(eq("expiredAccounts"), anyCollectionOf(ReinviteFormBean.class));
        
    }

    @Test
    public void testReinvite() throws Exception {

        ActivationAccount activationAccount = new ActivationAccount("ex_someVgrId", "http://someUrl.org",
                "Some message", "Some system");

        InvitePreferences preferences = new InvitePreferences();
        preferences.setCustomUrl("http://someUrl.org");
        preferences.setCustomMessage("Some message");
        preferences.setTitle("Some system");

        SimpleLdapUser ldapUser = new SimpleLdapUser("someDn");
        ldapUser.setCn("Test Testsson");
        ldapUser.setMail("some@mail.com");
        ldapUser.setTelephoneNumber("031-123456");
        ldapUser.setAttributeValue("externalStructurepersonDN", new String[]{"organisation1/division1",
                "organisation2/division1"});

        when(accountService.getAccount(any(ActivationCode.class))).thenReturn(activationAccount);
        when(invitePreferencesService.findByCustomUrl(anyString())).thenReturn(preferences);
        when(ldapService.getLdapUserByUid(anyString())).thenReturn(ldapUser);

        when(MessageBusUtil.sendSynchronousMessage(eq("vgr/account_invite"), any(Message.class), anyInt()))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<inviteUserResponse xmlns=\"http://portal.vgregion.se/inviteuser\">" +
                                "<userId>ex_apa</userId><statusCode>SUCCESS</statusCode>" +
                                "<message>the reply message</message></inviteUserResponse>";
                    }
                });

        ActionRequest request = mock(ActionRequest.class);
        Map<String, String> userInfo = new HashMap<String, String>();
        userInfo.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), "sponsor user name");
        when(request.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfo);

        ExtendedModelMap model = new ExtendedModelMap();// mock(Model.class);

        //The invocation to test
        reinviteController.reinvite(mock(ActivationCode.class), request, mock(ActionResponse.class), model);

        ReinviteFormBean bean = new ReinviteFormBean();
        bean.setEmail("some@mail.com");
        bean.setFullName("Test Testsson");
        bean.setOrganization("organisation1/division1, organisation2/division1");
        bean.setSponsor("sponsor user name");
        bean.setActivationCode(activationAccount.getActivationCode());
        bean.setSystem(preferences.getTitle());
        bean.setVgrId(activationAccount.getVgrId());
        bean.setCustomMessage(activationAccount.getCustomMessage());
        bean.setCustomUrl(activationAccount.getCustomUrl());
        bean.setSystem(activationAccount.getSystem());

        assertTrue(EqualsBuilder.reflectionEquals(model.get("reinvite"), bean));

    }
}
