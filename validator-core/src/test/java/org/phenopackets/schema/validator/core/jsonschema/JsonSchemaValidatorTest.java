package org.phenopackets.schema.validator.core.jsonschema;

import org.junit.jupiter.api.Test;

import java.io.IOException;
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

    /**
     * This example phenopacket does not contain anything except {"disney" : "donald"}
     * It does not contain an id or a metaData element and thus should fail.
     * @throws IOException
     */
    @Test
    public void testValidationOfSimpleInValidPhenopacket() throws IOException {
        String invalidSimplePhenopacket = fileFromClasspath("json/invalidSimplePhenopacket.json");
        JsonSchemaValidator validator = new JsonSchemaValidator();
        List<ValidationError> errors = validator.validate(invalidSimplePhenopacket);
        assertEquals(3, errors.size());
        ValidationError error = errors.get(0);
        assertEquals("required", error.getType());
        assertEquals("$.id: is missing but it is required", error.getMessage());
        error = errors.get(1);
        assertEquals("required", error.getType());
        assertEquals("$.metaData: is missing but it is required", error.getMessage());
        error = errors.get(2);
        assertEquals("additionalProperties", error.getType());
        assertEquals("$.disney: is not defined in the schema and the schema does not allow additional properties", error.getMessage());
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
//        for (ValidationError ve : errors) {
//            System.out.println(ve.getMessage());
//        }
        assertEquals(1, errors.size());
        ValidationError error = errors.get(0);
        assertEquals("additionalProperties", error.getType());
        assertEquals("$.phenotypicFeaturesMALFORMED: is not defined in the schema and the schema does not allow additional properties", error.getMessage());
    }


}
