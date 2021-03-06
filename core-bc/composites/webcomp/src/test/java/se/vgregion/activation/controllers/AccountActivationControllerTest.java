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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;
import se.vgregion.activation.formbeans.PasswordFormBean;
import se.vgregion.activation.util.JaxbUtil;
import se.vgregion.portal.activateuser.ActivateUserResponse;
import se.vgregion.portal.activateuser.ActivateUserStatusCodeType;

import javax.portlet.ActionResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * User: pabe
 * Date: 2011-04-27
 * Time: 15:53
 */
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(MessageBusUtil.class)
public class AccountActivationControllerTest {

    @Mock
    AccountService accountService;
    @InjectMocks
    AccountActivationController accountActivationController = new AccountActivationController(mock(Validator.class),
            mock(Validator.class), mock(Validator.class), mock(Validator.class));

    @Before
    public void setUp() throws Exception {
        JaxbUtil jaxbUtil = new JaxbUtil("se.vgregion.portal.activateuser");
        ReflectionTestUtils.setField(accountActivationController, "jaxbUtil", jaxbUtil);
    }

    @Test
    public void testSuccessfulActivateAccountWithOTP() throws Exception {

        //Given
        PasswordFormBean passwordFormBean = prepareTest();
        when(MessageBusUtil.sendSynchronousMessage(eq("vgr/account_activation"), any(Message.class), anyInt()))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><activateUserResponse xmlns=\"" +
                                "http://portal.vgregion.se/activateuser\">" +
                                "<userId>theuserid</userId><statusCode>SUCCESS</statusCode>" +
                                "<message>The message</message></activateUserResponse>";
                    }
                });

        ActionResponse actionResponse = mock(ActionResponse.class);

        //Do
        accountActivationController.activateAccountAsDominoUser(passwordFormBean, mock(BindingResult.class),
                actionResponse, mock(Model.class));

        //Then
        verify(actionResponse).setRenderParameter("success", "true");
    }

    @Test
    public void testTimeoutActivateAccountWithOTP() throws Exception {

        //Given
        PasswordFormBean passwordFormBean = prepareTest();
        when(MessageBusUtil.sendSynchronousMessage(eq("vgr/account_activation"), any(Message.class), anyInt()))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        MessageBusException messageBusException = new MessageBusException("No reply received for message...");
                        throw messageBusException; //Timeout
                    }
                });

        ActionResponse actionResponse = mock(ActionResponse.class);
        Model model = mock(Model.class);

        //Do
        accountActivationController.activateAccountAsDominoUser(passwordFormBean, mock(BindingResult.class),
                actionResponse, model);

        //Then
        verify(actionResponse).setRenderParameter("failure", "request.timeout");
    }

    @Test
    public void testErrorAnswerActivateAccountWithOTP() throws Exception {

        //Given
        PasswordFormBean passwordFormBean = prepareTest();
        when(MessageBusUtil.sendSynchronousMessage(eq("vgr/account_activation"), any(Message.class), anyInt()))
                .thenAnswer(new Answer<String>() {
                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        //Note: <statusCode>Error</statusCode>
                        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><activateUserResponse xmlns=\"" +
                                "http://portal.vgregion.se/activateuser\">" +
                                "<userId>theuserid</userId><statusCode>ERROR</statusCode>" +
                                "<message>The message</message></activateUserResponse>";
                    }
                });

        ActionResponse actionResponse = mock(ActionResponse.class);
        Model model = mock(Model.class);

        //Do
        accountActivationController.activateAccountAsDominoUser(passwordFormBean, mock(BindingResult.class),
                actionResponse, model);

        //Then
        verify(actionResponse).setRenderParameter("failure", "unknown.system.error");
    }

    private PasswordFormBean prepareTest() {
        ActivationAccount activationAccount = new ActivationAccount("someVgrId");

        when(accountService.getAccount(Matchers.<ActivationCode>any())).thenReturn(activationAccount);

        PasswordFormBean passwordFormBean = new PasswordFormBean();
        passwordFormBean.setActivationCode("1234");
        passwordFormBean.setPassword("newPassword");
        passwordFormBean.setVgrId("someVgrId");

        PowerMockito.mockStatic(MessageBusUtil.class);
        MessageBusUtil.init(mock(MessageBus.class), mock(MessageSender.class), mock(SynchronousMessageSender.class));
        return passwordFormBean;
    }

    @Test
    public void testMarshalActivateUserResponse() {
        ActivateUserResponse res = new ActivateUserResponse();
        res.setMessage("The message");
        res.setStatusCode(ActivateUserStatusCodeType.SUCCESS);
        res.setUserId("theuserid");

        StringWriter sw = new StringWriter();
        try {
            JAXBContext jc = JAXBContext.newInstance("se.vgregion.portal.activateuser");
            //Create marshaller
            Marshaller m = jc.createMarshaller();
            //Marshal object into file.
            m.marshal(res, sw);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><activateUserResponse xmlns=\"" +
                "http://portal.vgregion.se/activateuser\"><userId>theuserid</userId><statusCode>SUCCESS</statusCode>" +
                "<message>The message</message></activateUserResponse>", sw.toString());
    }


}
