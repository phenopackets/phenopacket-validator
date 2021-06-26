package org.phenopackets.schema.validator.core.jsonschema;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class JsonSchemaValidator {

    private JsonSchema phenopacketsBaseSchema;
    private  ObjectMapper objectMapper = new ObjectMapper();

    public JsonSchemaValidator() {
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V201909);
        try {
            InputStream baseSchemaStream = inputStreamFromClasspath("schema/phenopacket-general-schema.json");
            this.phenopacketsBaseSchema = schemaFactory.getSchema(baseSchemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Validate the file at fpath (assumed to be a Phenopacket formated in JSON) using the
     * base validation schema
     * @param fpath Path to a Phenopacket
     * @return List of {@link ValidationError} objects (empty list if there were no errors)
     */
    public List<ValidationError> validate(String fpath) {
        List<ValidationError> errors = new ArrayList<>();
        try {
            File f = new File(fpath);
            if (! f.isFile()) {
                throw new RuntimeException("Could not open file at \"" + f.getAbsolutePath() + "\"");
            }
            InputStream jsonStream = new FileInputStream(fpath);
            JsonNode json = objectMapper.readTree(jsonStream);
            Set<ValidationMessage> validationResult = phenopacketsBaseSchema.validate(json);
            if (! validationResult.isEmpty()) {
                validationResult.forEach(e -> errors.add(new ValidationError(e)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errors;
    }


    // TODO
    private List<ValidationError> validateWithAdditionalSchema(String fpath, String additionalSchemaPath) {
        List<ValidationError> errors = new ArrayList<>();
        try {
            File f = new File(fpath);
            if (! f.isFile()) {
                throw new RuntimeException("Could not open file at \"" + f.getAbsolutePath() + "\"");
            }
            InputStream jsonStream = new FileInputStream(fpath);
            JsonNode json = objectMapper.readTree(jsonStream);
            Set<ValidationMessage> validationResult = phenopacketsBaseSchema.validate(json);
            if (! validationResult.isEmpty()) {
                validationResult.forEach(e -> errors.add(new ValidationError(e)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Now validate against the additional schema
        // ...
        // add any new errors to the errors List.
        return errors;
    }



    /**
     * Get a resource from the maven src/main/resources directory
     * @param path
     * @return
     */
    private static InputStream inputStreamFromClasspath(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }


}
