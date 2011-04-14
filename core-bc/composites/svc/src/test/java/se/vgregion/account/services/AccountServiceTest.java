package se.vgregion.account.services;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.HibernateJdbcException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

@ContextConfiguration({ "classpath:spring/test-jpa-configuration.xml",
        "classpath:spring/account-activation-svc.xml" })
public class AccountServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Resource
    private AccountService service;
    private static final String EXAMPLE_URL = "http://example.com";
    private static final ActivationCode VALID_ACTIVATION_CODE = new ActivationCode("valid1");
    private static final ActivationCode INACTIVE_ACTIVATION_CODE = new ActivationCode("inactive");
    private static final ActivationCode INVALID_ACTIVATION_CODE = new ActivationCode("invalid");
    private static final String TEST_VGRID = "ex_test-vgrid";
    private static final String VALID_VGRID = "ex_valid-vgrid1";
    private static final String INVALID_VGRID = "ex_invalid-vgrid";
    private static final String INACTIVE_VGRID = "ex_inactive-vgrid";

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

        ActivationCode code = service.createAccount(TEST_VGRID, EXAMPLE_URL);
        assertNotNull(code);

        ActivationAccount account = service.getAccount(code);

        assertNotNull(account);
        assertEquals(TEST_VGRID, account.getVgrId());
        assertEquals(EXAMPLE_URL, account.getCustomUrl());
    }

    @Test
    public void shouldGetAccount() throws Exception {
        ActivationAccount account = service.getAccount(VALID_ACTIVATION_CODE);

        assertNotNull(account);
        assertEquals(VALID_VGRID, account.getVgrId());
    }

    @Test
    public void shouldGetCustomUrl() throws Exception {
        String customUrl = service.getCustomUrl(VALID_ACTIVATION_CODE);
        assertEquals(EXAMPLE_URL, customUrl);
    }

    @Test
    public void shouldRemoveAccount() throws Exception {
        ActivationAccount account = service.getAccount(VALID_ACTIVATION_CODE);
        assertNotNull(account);

        service.remove(VALID_ACTIVATION_CODE);

        account = service.getAccount(VALID_ACTIVATION_CODE);
        assertNull(account);
    }

    @Test(expected = HibernateJdbcException.class)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldFailToCreateAccountWithExistingVgrId() {
        service.createAccount(TEST_VGRID, EXAMPLE_URL);
        service.createAccount(TEST_VGRID, EXAMPLE_URL);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldReactivateAccount() throws Exception {
        ActivationAccount account = service.getAccount(INACTIVE_ACTIVATION_CODE);
        assertTrue(account.hasExpired());
        service.reactivate(account);
        assertFalse(account.hasExpired());
    }
}
