package nl.tudelft.sem.template.authentication.domain.user;

import static nl.tudelft.sem.template.authentication.domain.user.FieldValidation.validateField;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class FieldTests {

    @Test
    public void emptyField() {
        String field = "";
        assertFalse(validateField(field));
    }

    @Test
    public void nullField() {
        String field = null;
        assertFalse(validateField(field));
    }

    @Test
    public void longField() {
        String validField = "012345678901234567891234";
        assertTrue(validateField(validField));

        String invalidField = "0123456789012345678912345";
        assertFalse(validateField(invalidField));
    }

    @Test
    public void shortField() {
        String validField = "key";
        assertTrue(validateField(validField));

        String invalidField = "ke";
        assertFalse(validateField(invalidField));
    }
}
