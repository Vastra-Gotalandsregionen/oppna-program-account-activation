package se.vgregion.account.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.account.services.repository.ActivationAccountRepository;
import se.vgregion.activation.domain.ActivationAccount;
import se.vgregion.activation.domain.ActivationCode;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.Assert.*;

@ContextConfiguration({"classpath:spring/datasource-config.xml", "classpath:spring/activate-account-svc.xml"})
public class AccountServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Resource
    private AccountService service;

    @Autowired
    private ActivationAccountRepository repository;

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
    @Rollback(true)
    public void shouldReturnExpiredAccount() {
        //also add an entry with relative date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -45);
        ActivationAccount oldActivationAccount = new ActivationAccount("ex_expired_vgrid");
        oldActivationAccount.setExpireDate(calendar.getTime());
        repository.persist(oldActivationAccount);

        Collection<ActivationAccount> oldUnusedAccounts = service.getExpiredUnusedAccounts(45, 46);
        assertEquals(1, oldUnusedAccounts.size());
    }

    @Test
    @Rollback(false)
    public void shouldCreateAndReturnANewAccount() throws Exception {

        ActivationCode code = service.createAccount(TEST_VGRID, EXAMPLE_URL, "", "A system");
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

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldNotFailToCreateAccountWithExistingVgrId() {
        try {
            service.createAccount(TEST_VGRID, EXAMPLE_URL, "", "A system");
            service.createAccount(TEST_VGRID, EXAMPLE_URL, "", "A system");
        } catch (Exception ex) {
            fail();
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldReactivateAccount() throws Exception {
        ActivationAccount account = service.getAccount(INACTIVE_ACTIVATION_CODE);
        assertTrue(account.hasExpired());
        service.reactivate(account);
        assertFalse(account.hasExpired());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void shouldInactivateAccount() throws Exception {
        ActivationAccount account = service.getAccount(VALID_ACTIVATION_CODE);
        assertFalse(account.isUsed());
        service.inactivate(account);
        assertTrue(account.isUsed());
    }
}
