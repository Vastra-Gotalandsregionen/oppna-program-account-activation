package se.vgregion.activation.domain;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class ActivationAccountTest {
    private static final String VGR_ID = "ex_vgrid";
    private static final String CUSTOM_URL = "http://example.com";
    private static final String CUSTOM_MESSAGE = "Custom message";

    @Test
    public void shouldConstructActivationAccountWithVgrId() throws Exception {
        ActivationAccount account = new ActivationAccount(VGR_ID);
        shouldConstructActivationAccount(account);
    }

    @Test
    public void shouldConstructActivationAccountWithVgrIdAndCustomUrl() throws Exception {
        ActivationAccount account = new ActivationAccount(VGR_ID, CUSTOM_URL, CUSTOM_MESSAGE, "A system");
        assertEquals(CUSTOM_URL, account.getCustomUrl());
        shouldConstructActivationAccount(account);
    }

    private void shouldConstructActivationAccount(ActivationAccount account) {
        ActivationCode activationCode = account.getActivationCode();

        assertEquals(VGR_ID, account.getVgrId());
        assertNotNull(activationCode);
        if (activationCode != null) {
            assertFalse(StringUtils.isBlank(activationCode.getValue()));
        }

        Calendar inSixDays = Calendar.getInstance();
        inSixDays.add(Calendar.DAY_OF_YEAR, 6);

        Calendar inEightDays = Calendar.getInstance();
        inEightDays.add(Calendar.DAY_OF_YEAR, 8);

        assertEquals(account.getId(), account.getActivationCode());
        assertTrue(inSixDays.getTime().before(account.getExpireDate()));
        assertTrue(inEightDays.getTime().after(account.getExpireDate()));
    }

    @Test
    public void shouldBeExpiredIfExpiredDateIsNull() throws Exception {
        ActivationAccount account = new ActivationAccount();
        assertTrue(account.hasExpired());
    }

    @Test
    public void shouldBeExpiredIfDateIsLaterThanExpiredDate() throws Exception {
        ActivationAccount account = new ActivationAccount();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        account.setExpireDate(yesterday.getTime());
        assertTrue(account.hasExpired());
    }

    @Test
    public void shouldReturnValidExpiredDateOrNull() throws Exception {
        ActivationAccount account = new ActivationAccount();
        account.setExpireDate(null);
        assertNull(account.getExpireDate());
        account.setExpireDate(Calendar.getInstance().getTime());
        assertNotNull(account.getExpireDate());
    }

    @Test
    public void shouldBeInvalidAfterInvalidation() throws Exception {
        ActivationAccount account = new ActivationAccount();
        assertFalse(account.isUsed());
        account.inactivate();
        assertTrue(account.isUsed());
    }

}
