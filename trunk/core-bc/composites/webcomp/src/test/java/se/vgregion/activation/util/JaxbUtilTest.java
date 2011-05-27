package se.vgregion.activation.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import se.vgregion.portal.activateuser.ActivateUser;
import se.vgregion.portal.createuser.CreateUser;
import se.vgregion.portal.createuser.CreateUserResponse;
import se.vgregion.portal.createuser.CreateUserStatusCodeType;
import se.vgregion.portal.inviteuser.InviteUser;
import se.vgregion.portal.inviteuser.InviteUserResponse;
import se.vgregion.portal.inviteuser.InviteUserStatusCodeType;

import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 6/5-11
 * Time: 14:28
 */
public class JaxbUtilTest {
    private JaxbUtil jaxbUtilActivate;
    private JaxbUtil jaxbUtilCreate;
    private JaxbUtil jaxbUtilInvite;

    private ActivateUser activateObject;
    private CreateUser createObject;
    private CreateUserResponse createUserResponse;
    private InviteUserResponse inviteUserResponse;


    private InviteUser inviteObject;
    private String testXmlCreate;
    private String testXmlCreateResponse;
    private String testXmlActivate;
    private String testXmlInvite;
    private String testXmlInviteResponse;

    @Before
    public void setUp() throws Exception {
        initActivate();
        initCreate();
        initInvite();
    }

    private void initActivate() {
        jaxbUtilActivate = new JaxbUtil("se.vgregion.portal.activateuser");

        activateObject = new ActivateUser();
        activateObject.setActivationCode("apa");
        activateObject.setUserId("ex_apa");
        activateObject.setUserMail("apa@apa.nu");
        activateObject.setUserPassword("apaapa");

        testXmlActivate = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><activateUser xmlns=\"http://portal" +
                ".vgregion.se/activateuser\"><userId>ex_apa</userId><userMail>apa@apa.nu</userMail><activationCode>" +
                "apa</activationCode><userPassword>apaapa</userPassword></activateUser>";
    }

    private void initCreate() {
        jaxbUtilCreate = new JaxbUtil("se.vgregion.portal.createuser");

        createObject = new CreateUser();
        createObject.setUserFirstName("name");
        createObject.setUserSurName("sur");
        createObject.setUserMiddleName("middle");
        createObject.setExternStructurepersonDN("apa/bepa");
        createObject.setUserTelephoneNumber("123456");
        createObject.setUserMobileTelephoneNumber("0123456");
        createObject.setUserMail("apa@bepa.se");
        createObject.setSponsor("vgr_sponsor");
        createObject.setUserType("test");

        testXmlCreate = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<createUser xmlns=\"http://portal.vgregion.se/createuser\">" +
                "<userFirstName>name</userFirstName>" +
                "<userSurName>sur</userSurName>" +
                "<userMiddleName>middle</userMiddleName>" +
                "<externStructurepersonDN>apa/bepa</externStructurepersonDN>" +
                "<userTelephoneNumber>123456</userTelephoneNumber>" +
                "<userMobileTelephoneNumber>0123456</userMobileTelephoneNumber>" +
                "<userMail>apa@bepa.se</userMail>" +
                "<sponsor>vgr_sponsor</sponsor>" +
                "<userType>test</userType>" +
                "</createUser>";

        createUserResponse = new CreateUserResponse();
        createUserResponse.setMessage("message");
        createUserResponse.setStatusCode(CreateUserStatusCodeType.NEW_USER);
        createUserResponse.setVgrId("ex_test");

        testXmlCreateResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<createUserResponse xmlns=\"http://portal.vgregion.se/createuser\">" +
                "<vgrId>ex_test</vgrId>" +
                "<statusCode>NEW_USER</statusCode>" +
                "<message>message</message>" +
                "</createUserResponse>";
    }

    private void initInvite() {
        jaxbUtilInvite = new JaxbUtil("se.vgregion.portal.inviteuser");

        inviteObject = new InviteUser();
        inviteObject.setUserId("ex_apa");
        inviteObject.setCustomMessage("apa msg");
        inviteObject.setCustomURL("http://apa");

        inviteUserResponse = new InviteUserResponse();
        inviteUserResponse.setMessage("the reply message");
        inviteUserResponse.setStatusCode(InviteUserStatusCodeType.SUCCESS);
        inviteUserResponse.setUserId("ex_apa");

        testXmlInvite = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><inviteUser xmlns=\"http://portal.vgregion.se/inviteuser\"><userId>ex_apa</userId><customURL>http://apa</customURL><customMessage>apa msg</customMessage></inviteUser>";
        testXmlInviteResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><inviteUserResponse xmlns=\"http://portal.vgregion.se/inviteuser\"><userId>ex_apa</userId><statusCode>SUCCESS</statusCode><message>the reply message</message></inviteUserResponse>";
    }

    @Test
    public void testMarshalActivate() throws Exception {
        String result = jaxbUtilActivate.marshal(activateObject);

        assertEquals(testXmlActivate, result);
    }

    @Test
    public void testMarshalInvite() throws Exception {
        String result = jaxbUtilInvite.marshal(inviteObject);

        assertEquals(testXmlInvite, result);
    }

    @Test
    public void testMarshalCreate() throws Exception {
        initCreate();

        String result = jaxbUtilCreate.marshal(createObject);

        assertEquals(testXmlCreate, result);
    }

    @Test
    public void testMarshalCreateResponse() throws Exception {
        initCreate();

        String result = jaxbUtilCreate.marshal(createUserResponse);

        assertEquals(testXmlCreateResponse, result);
    }

    @Test
    public void testMarshalInviteResponse() throws Exception {
        initCreate();

        String result = jaxbUtilInvite.marshal(inviteUserResponse);

        assertEquals(testXmlInviteResponse, result);
    }

    @Test
    public void testUnmarshalAcitvate() throws Exception {
        ActivateUser result = jaxbUtilActivate.unmarshal(testXmlActivate);

        assertEquals(activateObject.getActivationCode(), result.getActivationCode());
        assertEquals(activateObject.getUserId(), result.getUserId());
        assertEquals(activateObject.getUserMail(), result.getUserMail());
        assertEquals(activateObject.getUserPassword(), result.getUserPassword());
    }

    @Test
    public void testUnmarshalCreate() throws Exception {
        CreateUser result = jaxbUtilCreate.unmarshal(testXmlCreate);

        assertTrue(EqualsBuilder.reflectionEquals(createObject, result));
    }
}
