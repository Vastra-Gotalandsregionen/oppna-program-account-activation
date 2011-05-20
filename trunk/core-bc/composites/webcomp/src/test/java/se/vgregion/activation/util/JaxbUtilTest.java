package se.vgregion.activation.util;

import org.junit.Before;
import org.junit.Test;
import se.vgregion.portal.activateuser.ActivateUser;
import se.vgregion.portal.createuser.CreateUser;

import static org.junit.Assert.assertEquals;

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
    private ActivateUser inviteObject;


    private String testXmlCreate;
    private String testXmlActivate;
    private String testXmlInvite;

    @Before
    public void setUp() throws Exception {
        initActivate();
        initCreate();

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
        createObject.setUserMiddleName("middle");
        createObject.setUserSurName("sur");
        createObject.setUserMail("apa@bepa.se");
        createObject.setExternStructurepersonDN("apa/bepa");
        createObject.setUserTelephoneNumber("123456");
        createObject.setUserMobileTelephoneNumber("0123456");
        createObject.setUserType("test");
        createObject.setSponsor("vgr_sponsor");

        testXmlCreate = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><createUser xmlns=\"http://portal.vgregion.se/createuser\"><userFirstName>name</userFirstName><userSurName>sur</userSurName><userMiddleName>middle</userMiddleName><externStructurepersonDN>apa/bepa</externStructurepersonDN><userTelephoneNumber>123456</userTelephoneNumber><userMobileTelephoneNumber>0123456</userMobileTelephoneNumber><userMail>apa@bepa.se</userMail><sponsor>vgr_sponsor</sponsor><userType>test</userType></createUser>";
    }

    @Test
    public void testMarshalActivate() throws Exception {
        String result = jaxbUtilActivate.marshal(activateObject);

        assertEquals(testXmlActivate, result);
    }

    @Test
    public void testMarshalCreate() throws Exception {
        initCreate();

        String result = jaxbUtilCreate.marshal(createObject);

        assertEquals(testXmlCreate, result);
    }

    @Test
    public void testUnmarshalAcitvate() throws Exception {
        ActivateUser result = jaxbUtilActivate.unmarshal(testXmlActivate);

        assertEquals(activateObject.getActivationCode(), result.getActivationCode());
        assertEquals(activateObject.getUserId(), result.getUserId());
        assertEquals(activateObject.getUserMail(), result.getUserMail());
        assertEquals(activateObject.getUserPassword(), result.getUserPassword());
    }
}
