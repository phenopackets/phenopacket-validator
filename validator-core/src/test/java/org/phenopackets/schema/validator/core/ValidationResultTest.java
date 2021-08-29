package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.validator.core.old.ValidationResult;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
class ValidationResultTest {

    @Test
    void failWontAcceptEmptyOrNullInput() {
        assertThrows(NullPointerException.class, () -> ValidationResult.fail(null));
        assertThrows(IllegalArgumentException.class, () -> ValidationResult.fail(""));
    }

    @Test
    void failReturnsInputMessage() {
        String message = "Sass that hoopy Frood, Ford Prefect?";
        ValidationResult instance = ValidationResult.fail(message);
        assertThat(instance.getMessage(), equalTo(message));
    }

    @Test
    void failMessageIsNotValid() {
        ValidationResult instance = ValidationResult.fail("wibble");
        assertFalse(instance.isValid());
    }

    @Test
    void passMessageIsValid() {
        ValidationResult instance = ValidationResult.pass();
        assertTrue(instance.isValid());
    }
}