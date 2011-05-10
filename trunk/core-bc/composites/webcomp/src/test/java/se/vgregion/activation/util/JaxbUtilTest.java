package se.vgregion.activation.util;

import org.junit.Before;
import org.junit.Test;
import se.vgregion.portal.ActivateUser;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: david
 * Date: 6/5-11
 * Time: 14:28
 */
public class JaxbUtilTest {
    private JaxbUtil jaxbUtil;

    private ActivateUser testObject;

    private String testXml;

    @Before
    public void setUp() throws Exception {
        jaxbUtil = new JaxbUtil("se.vgregion.portal");

        testObject = new ActivateUser();
        testObject.setActivationCode("apa");
        testObject.setUserId("ex_apa");
        testObject.setUserMail("apa@apa.nu");
        testObject.setUserPassword("apaapa");

        testXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<activateUser>" +
                "<userId>ex_apa</userId>" +
                "<userMail>apa@apa.nu</userMail>" +
                "<activationCode>apa</activationCode>" +
                "<userPassword>apaapa</userPassword>" +
                "</activateUser>";
    }

    @Test
    public void testMarshal() throws Exception {
        String result = jaxbUtil.marshal(testObject);

        assertEquals(testXml, result);
    }

    @Test
    public void testUnmarshal() throws Exception {
        ActivateUser result = jaxbUtil.unmarshal(testXml);

        assertEquals(testObject.getActivationCode(), result.getActivationCode());
        assertEquals(testObject.getUserId(), result.getUserId());
        assertEquals(testObject.getUserMail(), result.getUserMail());
        assertEquals(testObject.getUserPassword(), result.getUserPassword());
    }
}
