package org.phenopackets.schema.validator.core.jsonschema;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;
import org.phenopackets.schema.validator.core.validation.JsonValidator;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class JsonSchemaValidator implements JsonValidator {

    protected JsonSchema jsonSchema;
    protected  ObjectMapper objectMapper = new ObjectMapper();
    /** The latest version of the spec that is supported by our JSON SCHEMA library is 2019/09. */
    protected static final SpecVersion.VersionFlag VERSION_FLAG = SpecVersion.VersionFlag.V201909;
    protected File jsonFile;

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
            this.jsonSchema = schemaFactory.getSchema(baseSchemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonSchemaValidator() {

    }


    /**
     * Get a resource from the maven src/main/resources directory
     * @param path Path to a file resource
     * @return corresponding input stream
     */
    protected static InputStream inputStreamFromClasspath(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    /** Validate the file at fpath (assumed to be a Phenopacket formated in JSON) using the
     * base validation schema
     * @return List of {@link JsonValidationError} objects (empty list if there were no errors)
     */
    @Override
    public List<ValidationItem> validate() {
        List<ValidationItem> errors = new ArrayList<>();
        try {
            InputStream jsonStream = new FileInputStream(jsonFile);
            JsonNode json = objectMapper.readTree(jsonStream);
            Set<ValidationMessage> validationResult = jsonSchema.validate(json);
            if (! validationResult.isEmpty()) {
                validationResult.forEach(e -> errors.add(new JsonValidationError(e)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errors;
    }
}
