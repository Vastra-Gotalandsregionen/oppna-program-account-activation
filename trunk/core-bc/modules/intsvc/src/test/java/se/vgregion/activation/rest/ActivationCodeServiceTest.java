package se.vgregion.activation.rest;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.RequestLogHandler;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.account.services.AccountService;
import se.vgregion.activation.api.ActivationAccountDTO;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: pabe
 * Date: 2011-08-31
 * Time: 09:52
 */

@ContextConfiguration(locations = {"/testContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ActivationCodeServiceTest {

    @Autowired
    @Qualifier(value = "client")
    private ActivationCodeService activationCodeProxyService;

    private org.mortbay.jetty.Server jettyServer;

    //This method is used by Spring as a factory method for the server service.
    public static AccountService mockAccountService() {
        AccountService accountService = mock(AccountService.class);

        ActivationAccount aa1 = new ActivationAccount("id1", "http://example.com", "custom msg1", "system1");
        ActivationAccount aa2 = new ActivationAccount("id2", "http://google.com", "custom msg2", "system2");
        List<ActivationAccount> activationAccounts = Arrays.asList(aa1, aa2);

        when(accountService.getAllValidAccounts()).thenReturn(activationAccounts);

        when(accountService.getAccount(any(ActivationCode.class))).thenReturn(aa2);

        return accountService;
    }

    @Test
    public void testAllActivationCode() {
        Collection<ActivationAccountDTO> allActivationCodes = activationCodeProxyService.getAllActivationCode();

        assertEquals(2, allActivationCodes.size());
    }

    @Test
    public void testGetActivationCode() throws MalformedURLException {
        ActivationAccountDTO activationAccountDTO = activationCodeProxyService.getActivationCode("id1");
        assertEquals(activationAccountDTO.getCustomUrl(), new URL("http://example.com"));
    }

    @Test
    public void testGetActivationFormUrl() throws InterruptedException {
        String activationFormUrl = activationCodeProxyService.getActivationFormUrl(new ActivationCode("anyValue"));
        assertTrue(activationFormUrl.contains("anyValue"));

    }

    @After
    public void tearDown() throws Exception {
        jettyServer.stop();
    }

    @Before
    public void setup() throws Exception {
        SpringBusFactory bf = new SpringBusFactory();
        final Bus customBus = bf.createBus("testContext-web.xml");

        jettyServer = new org.mortbay.jetty.Server();

        setupSslSocketConnector();

        Context context = new Context();
        context.setContextPath("/");

        ServletHandler servletHandler = new ServletHandler();

        //Had much problems to make Spring set the right destinations which, in turn,
        //depended on that Spring didn't get any good context and therefore the wrong
        //bus. This was a workaround but there may be a way to configure it right
        //directly.
        CXFServlet cxfServlet = new CXFServlet() {
            @Override
            protected void replaceDestinationFactory() {
                this.bus = customBus;
                super.replaceDestinationFactory();
            }
        };

        ServletHolder servletHolder = new ServletHolder(cxfServlet);
        servletHolder.setInitParameter("config-location", "testContext-web.xml");
        servletHolder.setName("CXFServlet");
        servletHolder.setServlet(cxfServlet);

        servletHandler.addServletWithMapping(servletHolder, "/*");

        context.setServletHandler(servletHandler);

        jettyServer.addHandler(context);
        jettyServer.addHandler(new DefaultHandler());
        jettyServer.addHandler(new RequestLogHandler());
        jettyServer.start();
    }

    private void setupSslSocketConnector() {
        SslSocketConnector sslSocketConnector = new SslSocketConnector();
        sslSocketConnector.setPort(8989);
        sslSocketConnector.setNeedClientAuth(true);

        String serverKeystore = this.getClass().getClassLoader().getResource("cert/serverkeystore.jks").getPath();
        sslSocketConnector.setKeystore(serverKeystore);
        sslSocketConnector.setKeyPassword("serverpass");
        String serverTruststore = this.getClass().getClassLoader().getResource("cert/servertruststore.jks").getPath();
        sslSocketConnector.setTruststore(serverTruststore);
        sslSocketConnector.setTrustPassword("serverpass");
        jettyServer.addConnector(sslSocketConnector);
    }
}
