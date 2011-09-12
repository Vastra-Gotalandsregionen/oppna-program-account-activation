package se.vgregion.activation.validators;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import se.vgregion.activation.formbeans.PasswordFormBean;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: pabe
 * Date: 2011-05-24
 * Time: 12:44
 */
public class PasswordStrengthValidatorTest {

    private PasswordFormBean passwordFormBean;
    private PasswordStrengthValidator validator;
    private Errors errors;
    private HashMap target;

    @Before
    public void setUp() throws Exception {
        passwordFormBean = new PasswordFormBean();
        validator = new PasswordStrengthValidator();
        target = new HashMap();
        errors = new MapBindingResult(target, "password");
    }

    @Test
    public void testValidateNoNumbersAndTooShort() throws Exception {
        passwordFormBean.setPassword("apa");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 2);
    }

    @Test
    public void testValidateNoNumbersAndTooShortAndIllegalCharacter() throws Exception {
        passwordFormBean.setPassword("apa_");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 3);
    }

    @Test
    public void testValidateNoNumbers() throws Exception {
        passwordFormBean.setPassword("apabanan");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

    @Test
    public void testValidateIllegalCharacter1() throws Exception {
        passwordFormBean.setPassword("apa banan23");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

    @Test
    public void testValidateIllegalCharacter2() throws Exception {
        passwordFormBean.setPassword("apa23   #5banan");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

    @Test
    public void testValidateIllegalCharacter3() throws Exception {
        passwordFormBean.setPassword("23apaåäöbanan");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

    @Test
    public void testValidateIllegalCharacter4() throws Exception {
        passwordFormBean.setPassword("@23apabanan");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

    @Test
    public void testValidateIllegalCharacterShortWithoutNumbersOrLetters() throws Exception {
        passwordFormBean.setPassword("&");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 3);
    }

    @Test
    public void testValidateNoLetters() throws Exception {
        passwordFormBean.setPassword("123456");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

    @Test
    public void testValidateCorrect1() throws Exception {
        passwordFormBean.setPassword("hejsan3");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 0);
    }

    @Test
    public void testValidateCorrect2() throws Exception {
        passwordFormBean.setPassword("3hejsan3");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 0);
    }

    @Test
    public void testValidateCorrect3() throws Exception {
        passwordFormBean.setPassword("333333f");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 0);
    }

    @Test
    public void testValidateNull() throws Exception {
        passwordFormBean.setPassword(null);
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

    @Test
    public void testValidateEmpty() throws Exception {
        passwordFormBean.setPassword("");
        target.put("password", passwordFormBean.getPassword());
        validator.validate(passwordFormBean, errors);
        assertEquals(errors.getAllErrors().size(), 1);
    }

}
