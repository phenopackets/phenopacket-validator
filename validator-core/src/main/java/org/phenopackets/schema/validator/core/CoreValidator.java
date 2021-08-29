package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.validator.core.configval.ConfigFileValidator;
import org.phenopackets.schema.validator.core.configval.DefaultRareDiseaseConfigValidator;
import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;
import org.phenopackets.schema.validator.core.jsonschema.JsonSchemaValidator;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.File;
import java.util.List;

/**
 * Entry point for the library. Performs JSON schema validation. If an addition config file is provided, it performs
 * additional validation. Several default validation files are provided that can be used with command line flags
 */
public class CoreValidator {

    List<ValidationItem> validationErrors;

    public static enum ValidationType { UNKNOWN, RARE_DISEASE_VALIDATION}

    public CoreValidator(String phenopacketPath) {
        File phenopacket = new File(phenopacketPath);
        if (! phenopacket.isFile()) {
            throw new PhenopacketValidatorRuntimeException("Could not find file \"" + phenopacketPath + "\"");
        }
        JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator(phenopacket);
        validationErrors = jsonSchemaValidator.validate();
    }

    public CoreValidator(String phenopacketPath, ValidationType validationType) {
        File phenopacket = new File(phenopacketPath);
        if (! phenopacket.isFile()) {
            throw new PhenopacketValidatorRuntimeException("Could not find file \"" + phenopacketPath + "\"");
        }
        JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator(phenopacket);
        validationErrors = jsonSchemaValidator.validate();
        switch (validationType) {
            case RARE_DISEASE_VALIDATION:
                ConfigFileValidator validator = new DefaultRareDiseaseConfigValidator(phenopacket);
                List<ValidationItem> specErrors = validator.validate();
                validationErrors.addAll(specErrors);
                validationErrors.addAll(specErrors);
                break;
        }
    }

    public List<? extends ValidationItem> getValidationErrors() {
        return validationErrors;
    }
}
