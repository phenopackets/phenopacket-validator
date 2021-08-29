package org.phenopackets.schema.validator.core.jsonschema;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;
import org.phenopackets.schema.validator.core.validation.PhenopacketValidator;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class JsonSchemaValidator implements PhenopacketValidator {

    private JsonSchema phenopacketsBaseSchema;
    private  ObjectMapper objectMapper = new ObjectMapper();
    /** The latest version of the spec that is supported by our JSON SCHEMA library is 2019/09. */
    private static final SpecVersion.VersionFlag VERSION_FLAG = SpecVersion.VersionFlag.V201909;
    private File jsonFile;

    /**
     * Constructor for validation using JSON Schema
     * @param f a JSON file.
     */
    public JsonSchemaValidator(File f) {
        this.jsonFile = f;
        if (! f.isFile()) {
            throw new PhenopacketValidatorRuntimeException("Could not open file at \"" + f.getAbsolutePath() + "\"");
        }
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(VERSION_FLAG);
        try {
            InputStream baseSchemaStream = inputStreamFromClasspath("schema/phenopacket-general-schema.json");
            this.phenopacketsBaseSchema = schemaFactory.getSchema(baseSchemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Get a resource from the maven src/main/resources directory
     * @param path
     * @return
     */
    private static InputStream inputStreamFromClasspath(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    /** Validate the file at fpath (assumed to be a Phenopacket formated in JSON) using the
     * base validation schema
     * @return List of {@link JsonValidationError} objects (empty list if there were no errors)
     */
    @Override
    public List<? extends ValidationItem> validate() {
        List<JsonValidationError> errors = new ArrayList<>();
        try {
            InputStream jsonStream = new FileInputStream(jsonFile);
            JsonNode json = objectMapper.readTree(jsonStream);
            Set<ValidationMessage> validationResult = phenopacketsBaseSchema.validate(json);
            if (! validationResult.isEmpty()) {
                validationResult.forEach(e -> errors.add(new JsonValidationError(e)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errors;
    }
}
