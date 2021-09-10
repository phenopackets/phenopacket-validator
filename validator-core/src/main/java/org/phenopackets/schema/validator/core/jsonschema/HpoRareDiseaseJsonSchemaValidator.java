package org.phenopackets.schema.validator.core.jsonschema;


import com.networknt.schema.JsonSchemaFactory;

import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;


import java.io.File;
import java.io.InputStream;

/**
 * This class implements additional validation of a phenopacket that is intended to be used
 * for HPO rare disease phenotyping. By assumption, the phenopacket will have been first
 * checked agoinst the generic specification. This class performs validation with the
 * file {@code hpo-rare-disease-schema.json}.
 * @author Peter N Robinson
 */
public class HpoRareDiseaseJsonSchemaValidator extends JsonSchemaValidator {


    public HpoRareDiseaseJsonSchemaValidator(File f) {
        this.jsonFile = f;
        if (! f.isFile()) {
            throw new PhenopacketValidatorRuntimeException("Could not open file at \"" + f.getAbsolutePath() + "\"");
        }
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(VERSION_FLAG);
        try {
            InputStream baseSchemaStream = inputStreamFromClasspath("schema/hpo-rare-disease-schema.json");
            this.jsonSchema = schemaFactory.getSchema(baseSchemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
