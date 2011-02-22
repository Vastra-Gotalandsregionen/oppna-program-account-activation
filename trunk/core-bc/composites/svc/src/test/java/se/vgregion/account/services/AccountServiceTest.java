package se.vgregion.account.services;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration("classpath:AccountServiceTest-context.xml")
public class AccountServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    AccountService service;

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
}
