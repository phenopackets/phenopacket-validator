package org.phenopackets.schema.validator.core.jsonschema;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaValidatorTest {

    private static String fileFromClasspath(String path) {
        return Thread.currentThread().getContextClassLoader().getResource(path).getPath();
    }

    @Test
    public void testValidationOfSimpleValidPhenopacket() throws IOException {
        String validSimplePhenopacket = fileFromClasspath("json/validSimplePhenopacket.json");
        JsonSchemaValidator validator = new JsonSchemaValidator();
        List<ValidationError> errors = validator.validate(validSimplePhenopacket);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidationOfSimpleInValidPhenopacket() throws IOException {
        String validSimplePhenopacket = fileFromClasspath("json/invalidSimplePhenopacket.json");
        JsonSchemaValidator validator = new JsonSchemaValidator();
        List<ValidationError> errors = validator.validate(validSimplePhenopacket);
        assertEquals(1, errors.size());
        ValidationError error = errors.get(0);
        assertEquals("required", error.getType());
        assertEquals("$.id: is missing but it is required", error.getMessage());
    }

    @Test
    public void testRareDiseaseBethlemahmValidPhenopacket() throws IOException {
        String validSimplePhenopacket = fileFromClasspath("json/bethlehamMyopathyExample.json");
        JsonSchemaValidator validator = new JsonSchemaValidator();
        List<ValidationError> errors = validator.validate(validSimplePhenopacket);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testRareDiseaseBethlemahmInvalidValidPhenopacket() throws IOException {
        String validSimplePhenopacket = fileFromClasspath("json/bethlehamMyopathyInvalidExample.json");
        JsonSchemaValidator validator = new JsonSchemaValidator();
        List<ValidationError> errors = validator.validate(validSimplePhenopacket);
        assertEquals(1, errors.size());
        ValidationError error = errors.get(0);
        assertEquals("required", error.getType());
        assertEquals("$.id: is missing but it is required", error.getMessage());
    }


}
