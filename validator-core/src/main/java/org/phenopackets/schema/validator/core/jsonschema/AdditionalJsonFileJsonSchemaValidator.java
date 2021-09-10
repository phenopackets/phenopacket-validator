package org.phenopackets.schema.validator.core.jsonschema;

import com.networknt.schema.JsonSchemaFactory;
import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AdditionalJsonFileJsonSchemaValidator extends JsonSchemaValidator{

    public AdditionalJsonFileJsonSchemaValidator(File phenopacketFile, File jsonFile) {
        this.jsonFile = phenopacketFile;
        if (! phenopacketFile.isFile()) {
            throw new PhenopacketValidatorRuntimeException("Could not open file at \"" + phenopacketFile.getAbsolutePath() + "\"");
        }
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(VERSION_FLAG);
        try {
            InputStream targetStream = new FileInputStream(jsonFile);
            this.jsonSchema = schemaFactory.getSchema(targetStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
