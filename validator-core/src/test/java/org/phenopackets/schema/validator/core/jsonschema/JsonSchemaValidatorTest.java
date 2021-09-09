package org.phenopackets.schema.validator.core.jsonschema;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.validator.core.validation.ErrorType;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonSchemaValidatorTest {

    private static File fileFromClasspath(String path) {
        String fname = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
        return new File(fname);
    }

    @Test
    public void testValidationOfSimpleValidPhenopacket() throws IOException {
        File validSimplePhenopacket = fileFromClasspath("json/validSimplePhenopacket.json");
        JsonSchemaValidator validator = new JsonSchemaValidator(validSimplePhenopacket);
        List<? extends ValidationItem> errors = validator.validate();
        assertTrue(errors.isEmpty());
    }

    /**
     * This example phenopacket does not contain anything except {"disney" : "donald"}
     * It does not contain an id or a metaData element and thus should fail.
     */
    @Test
    public void testValidationOfSimpleInValidPhenopacket() {
        File invalidSimplePhenopacket = fileFromClasspath("json/invalidSimplePhenopacket.json");
        JsonSchemaValidator validator = new JsonSchemaValidator(invalidSimplePhenopacket);
        List<? extends ValidationItem> errors = validator.validate();
        assertEquals(3, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(ErrorType.JSON_REQUIRED, error.errorType());
        assertEquals("$.id: is missing but it is required", error.message());
        error = errors.get(1);
        assertEquals(ErrorType.JSON_REQUIRED, error.errorType());
        assertEquals("$.metaData: is missing but it is required", error.message());
        error = errors.get(2);
        assertEquals(ErrorType.JSON_ADDITIONAL_PROPERTIES, error.errorType());
        assertEquals("$.disney: is not defined in the schema and the schema does not allow additional properties", error.message());
    }

    @Test
    public void testRareDiseaseBethlemahmValidPhenopacket() throws IOException {
        File myopathyPhenopacket = fileFromClasspath("json/bethlehamMyopathyExample.json");
        JsonSchemaValidator validator = new JsonSchemaValidator(myopathyPhenopacket);
        List<? extends ValidationItem> errors = validator.validate();
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testRareDiseaseBethlemahmInvalidValidPhenopacket() throws IOException {
        File invalidMyopathyPhenopacket = fileFromClasspath("json/bethlehamMyopathyInvalidExample.json");
        JsonSchemaValidator validator = new JsonSchemaValidator(invalidMyopathyPhenopacket);
        List<? extends ValidationItem> errors = validator.validate();
//        for (ValidationError ve : errors) {
//            System.out.println(ve.getMessage());
//        }
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(ErrorType.JSON_ADDITIONAL_PROPERTIES, error.errorType());
        assertEquals("$.phenotypicFeaturesMALFORMED: is not defined in the schema and the schema does not allow additional properties", error.message());
    }


}
