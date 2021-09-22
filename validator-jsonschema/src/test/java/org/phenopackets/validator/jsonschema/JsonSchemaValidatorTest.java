package org.phenopackets.validator.jsonschema;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.validator.testdatagen.RareDiseasePhenopacket;
import org.phenopackets.validator.testdatagen.SimplePhenopacket;
import org.phenopackets.validator.core.ErrorType;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.google.protobuf.util.JsonFormat;

import static org.junit.jupiter.api.Assertions.*;

public class JsonSchemaValidatorTest {

    private static final ClasspathJsonSchemaValidatorFactory FACTORY = ClasspathJsonSchemaValidatorFactory.defaultValidators();

    private static final SimplePhenopacket simplePhenopacket = new SimplePhenopacket();

    private static final RareDiseasePhenopacket rareDiseasePhenopacket = new RareDiseasePhenopacket();

    private static File fileFromClasspath(String path) {
        String fname = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource(path)).getPath();
        return new File(fname);
    }

    @Test
    public void testValidationOfSimpleValidPhenopacket() throws Exception {
        JsonSchemaValidator validator = FACTORY.getValidatorForType(ValidatorInfo.generic()).get();
        Phenopacket phenopacket = simplePhenopacket.getPhenopacket();
        String json =  JsonFormat.printer().print(phenopacket);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertTrue(errors.isEmpty());
        // the Phenopacket is not valid if we remove the id
        phenopacket = Phenopacket.newBuilder(phenopacket).clearId().build();
        json =  JsonFormat.printer().print(phenopacket);
        errors = validator.validate(json);
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(ErrorType.JSON_REQUIRED, error.errorType());
        assertEquals("$.id: is missing but it is required", error.message());
    }

    /**
     * This example phenopacket does not contain anything except {"disney" : "donald"}
     * It does not contain an id or a metaData element and thus should fail.
     */
    @Test
    public void testValidationOfSimpleInValidPhenopacket() throws Exception {
        JsonSchemaValidator validator = FACTORY.getValidatorForType(ValidatorInfo.generic()).get();

        String invalidPhenopacketJson = "{\"disney\" : \"donald\"}";

        List<? extends ValidationItem> errors = validator.validate(invalidPhenopacketJson);

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
    public void testRareDiseaseBethlemahmValidPhenopacket() throws Exception {
        JsonSchemaValidator validator = FACTORY.getValidatorForType(ValidatorInfo.rareDiseaseValidation()).get();

        Phenopacket bethlehamMyopathy = rareDiseasePhenopacket.getPhenopacket();
        String json =  JsonFormat.printer().print(bethlehamMyopathy);
        List<? extends ValidationItem> errors = validator.validate(json);

        assertTrue(errors.isEmpty());
    }

    @Test
    @Disabled // TODO - we should rework the testing strategy to invalidate a valid phenopacket and check that it raises the expected error
    public void testRareDiseaseBethlemahmInvalidValidPhenopacket() throws IOException {
        JsonSchemaValidator validator = FACTORY.getValidatorForType(ValidatorInfo.rareDiseaseValidation()).get();

        File invalidMyopathyPhenopacket = fileFromClasspath("json/bethlehamMyopathyInvalidExample.json");
        List<? extends ValidationItem> errors = validator.validate(invalidMyopathyPhenopacket);
//        for (ValidationError ve : errors) {
//            System.out.println(ve.getMessage());
//        }
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(ErrorType.JSON_ADDITIONAL_PROPERTIES, error.errorType());
        assertEquals("$.phenotypicFeaturesMALFORMED: is not defined in the schema and the schema does not allow additional properties", error.message());
    }


}