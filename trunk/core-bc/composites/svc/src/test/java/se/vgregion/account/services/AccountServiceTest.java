package se.vgregion.account.services;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateJdbcException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

@ContextConfiguration({ "classpath:spring/test-jpa-configuration.xml",
        "classpath:spring/account-activation-svc.xml" })
public class AccountServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Resource
    private AccountService service;

    @Before
    public void setUp() throws Exception {
        executeSqlScript("classpath:dbsetup/test-data.sql", false);
    }

    @After
    public void tearDown() throws Exception {
        executeSqlScript("classpath:dbsetup/drop-test-data.sql", false);
    }

    @Test
    public void shouldReturnAllValidAccounts() throws Exception {
        assertEquals(2, service.getAllValidAccounts().size());
    }

    @Test
    @Rollback(false)
    public void shouldCreateAndReturnANewAccount() throws Exception {
        final String vgrId = "ex_vgrid-test";
        final String url = "http://example.com";

        ActivationCode code = service.createAccount(vgrId, url);
        assertNotNull(code);

        ActivationAccount account = service.getAccount(code);

        assertNotNull(account);
        assertEquals(vgrId, account.getVgrId());
        assertEquals(url, account.getCustomUrl());
    }

    @Test
    public void shouldGetAccount() throws Exception {
        ActivationCode code = new ActivationCode("asdfasdf1");
        ActivationAccount account = service.getAccount(code);

        assertNotNull(account);
        assertEquals("ex_vgrid1", account.getVgrId());
    }

    @Test
    public void shouldGetCustomUrl() throws Exception {
        ActivationCode code = new ActivationCode("asdfasdf1");
        String customUrl = service.getCustomUrl(code);

        assertEquals("http://example.com", customUrl);
    }

    @Test
    public void shouldRemoveAccount() throws Exception {
        ActivationCode code = new ActivationCode("asdfasdf1");
        ActivationAccount account = service.getAccount(code);
        assertNotNull(account);

        service.remove(code);

        account = service.getAccount(code);
        assertNull(account);
    }

    @Ignore
    @Test(expected = HibernateJdbcException.class)
    @Rollback(false)
    public void shouldFailToCreateAccountWithExistingVgrId() {
        final String vgrId = "ex_vgrid-test";
        createDuplicateAccount(vgrId);
    }

    private void createDuplicateAccount(String vgrId) {
        final String url = "http://example.com";
        service.createAccount(vgrId, url);
        service.createAccount(vgrId, url);
    }
}
