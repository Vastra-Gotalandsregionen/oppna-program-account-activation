package se.vgregion.activation.controllers;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusException;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.messaging.sender.MessageSender;
import com.liferay.portal.kernel.messaging.sender.SynchronousMessageSender;
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
import org.springframework.mock.web.portlet.MockResourceResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.support.SessionStatus;
import se.vgregion.account.services.InvitePreferencesService;
import se.vgregion.account.services.StructureService;
import se.vgregion.activation.formbeans.ExternalUserFormBean;
import se.vgregion.activation.validators.ExternalUserValidator;
import se.vgregion.create.domain.InvitePreferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * User: pabe
 * Date: 2011-05-31
 * Time: 08:43
 */
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(MessageBusUtil.class)
public class InviteControllerTest {

    @Mock
    private InvitePreferencesService invitePreferencesService;
    @Mock
    private StructureService structureService;

    @InjectMocks
    private InviteController inviteController = new InviteController();

    private ExternalUserFormBean externalUserFormBean;
    private InvitePreferences invitePreferences;

    @Before
    public void setup() {
        PowerMockito.mockStatic(MessageBusUtil.class);
        MessageBusUtil.init(mock(MessageBus.class), mock(MessageSender.class), mock(SynchronousMessageSender.class));
        ExternalUserValidator externalUserValidator = new ExternalUserValidator();
        ReflectionTestUtils.setField(inviteController, "externalUserValidator", externalUserValidator);

        externalUserFormBean = new ExternalUserFormBean();
        externalUserFormBean.setUserType("");
        externalUserFormBean.setSponsorVgrId("sponsorId");
        externalUserFormBean.setDateLimit("2099-12-31");
        externalUserFormBean.setEmail("some@email.org");
        externalUserFormBean.setExternStructurePersonDn("org1/div1");
        externalUserFormBean.setMiddleName("");
        externalUserFormBean.setName("Arne");
        externalUserFormBean.setSurname("Gates");
        externalUserFormBean.setPhone("031-123456");
        invitePreferences = new InvitePreferences();
        invitePreferences.setCustomMessage("custom message");
        invitePreferences.setCustomUrl("http://google.com");
        invitePreferences.setTitle("Super system");
        externalUserFormBean.setInvitePreferences(invitePreferences);
    }

    @Test
    public void testInvite() throws Exception {

        when(MessageBusUtil.sendSynchronousMessage(eq("vgr/account_create"), any(Message.class), anyInt()))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                                "<createUserResponse xmlns=\"http://portal.vgregion.se/createuser\">" +
                                "<vgrId>ex_test</vgrId>" +
                                "<statusCode>NEW_USER</statusCode>" +
                                "<message>message</message>" +
                                "</createUserResponse>";
                    }
                });

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
        userInfo.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), "sponsorId");
        when(request.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfo);

        Model model = mock(Model.class);

        //The invocation to test
        ActionResponse response = mock(ActionResponse.class);
        inviteController.invite(externalUserFormBean, mock(BindingResult.class), model,
                mock(SessionStatus.class), request, response);

        verify(response).setRenderParameter("success", "success");

    }

    @Test
    public void testInviteWithException() throws Exception {

        when(MessageBusUtil.sendSynchronousMessage(eq("vgr/account_create"), any(Message.class), anyInt()))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        Exception ex = new ConnectException("test message");
                        throw new MessageBusException(ex);
                    }
                });

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
        userInfo.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), "sponsorId");
        when(request.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfo);

        Model model = mock(Model.class);

        //The invocation to test
        ActionResponse response = mock(ActionResponse.class);
        inviteController.invite(externalUserFormBean, mock(BindingResult.class), model,
                mock(SessionStatus.class), request, response);

        verify(response).setRenderParameter("unresponsive", "connection.failed");

    }

    @Test
    public void testSearchStructure() {
        when(structureService.search(anyString())).thenReturn(Arrays.asList("org1/div1", "org1/div2"));

        MockResourceResponse response = new MockResourceResponse();
        inviteController.searchStructure("some query", response);
        try {
            assertEquals("[\"org1/div1\",\"org1/div2\"]", response.getContentAsString());
        } catch (UnsupportedEncodingException e) {
            fail();
        }
    }

    @Test
    public void testShowExternalUserInvite() {
        externalUserFormBean.setSponsorVgrId(null);

        RenderRequest renderRequest = mock(RenderRequest.class);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), "sponsorId");

        when(renderRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(map);

        inviteController.showExternalUserInvite(externalUserFormBean, mock(Model.class), renderRequest);

        assertEquals("sponsorId", externalUserFormBean.getSponsorVgrId());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()),
                externalUserFormBean.getDateLimit());
    }

    @Test
    public void testShowExternalUserInviteWithoutBeingLoggedIn() {
        externalUserFormBean.setSponsorVgrId(null);

        RenderRequest renderRequest = mock(RenderRequest.class);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), null);

        when(renderRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(map);

        try {
            inviteController.showExternalUserInvite(externalUserFormBean, mock(Model.class), renderRequest);
            fail();
        } catch (IllegalStateException ex) {
            //test succeeds
        } catch (Exception ex) {
            //any other exception i.e.
            fail();
        }
    }

    @Test
    public void testShowExternalUserInviteAsExternalUser() {
        externalUserFormBean.setSponsorVgrId(null);

        RenderRequest renderRequest = mock(RenderRequest.class);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), "ex_apa");

        when(renderRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(map);

        try {
            inviteController.showExternalUserInvite(externalUserFormBean, mock(Model.class), renderRequest);
            fail();
        } catch (IllegalStateException ex) {
            //test succeeds
        } catch (Exception ex) {
            //any other exception i.e.
            fail();
        }
    }

    @Test
    public void testShowExternalUserInviteWithErrors() {
        externalUserFormBean.setSponsorVgrId(null);

        RenderRequest renderRequest = mock(RenderRequest.class);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(PortletRequest.P3PUserInfos.USER_LOGIN_ID.toString(), "sponsorId");

        when(renderRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(map);

        Model model = mock(Model.class);
        HashMap<String, Object> modelMap = new HashMap<String, Object>();
        Errors errors = mock(Errors.class);
        modelMap.put("errors", errors);
        when(model.asMap()).thenReturn(modelMap);
        inviteController.showExternalUserInvite(externalUserFormBean, model, renderRequest);

        verify(model).addAttribute("org.springframework.validation.BindingResult.externalUserFormBean", errors);
    }
}
