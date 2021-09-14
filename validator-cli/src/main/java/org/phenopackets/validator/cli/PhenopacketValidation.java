package org.phenopackets.validator.cli;

import org.phenopackets.validator.core.PhenopacketValidator;
import org.phenopackets.validator.core.PhenopacketValidatorFactory;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Entry point for the library. Performs JSON schema validation. If an additional config file is provided, it performs
 * additional validation. Several default validation files are provided that can be used with command line flags.
 * For instance, adding the --rare flag will additionally apply the {@code hpo-rare-disease-schema.json} specification.
 * @author Peter N Robinson
 */
public class PhenopacketValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(PhenopacketValidation.class);

    private final PhenopacketValidatorFactory validatorFactory;

    public PhenopacketValidation(PhenopacketValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

//    List<ValidationItem> validationErrors;

//
//    /** Can be used to validate a Phenopacket against the built-in additional JSON-Schema files,
//     * including HPO-rare disease TODO add another two examples.
//     * @param phenopacketPath Path to the phenopacket file
//     * @param validationType One of the built-in types, see {@link ValidationType}.
//     */
//    public PhenopacketValidation(String phenopacketPath, ValidationType validationType) {
//        phenopacket = initPhenopacketFile(phenopacketPath);
//        validationErrors = genericValidation();
//
//        switch (validationType) {
//            case RARE_DISEASE_VALIDATION:
//                List<ValidationItem> specErrors = hpoRareDiseaseValidation();
//                validationErrors.addAll(specErrors);
//                break;
//        }
//    }
//
//    /**
//     * This constructor can be used with one or more user defined JSON spec files that are passed from the command line
//     * @param phenopacketPath Path to the phenopacket file
//     * @param jsonSchemPaths Paths to user-defined JSON Schema files
//     */
//    public PhenopacketValidation(String phenopacketPath, String... jsonSchemPaths) {
//        phenopacket = initPhenopacketFile(phenopacketPath);
//        validationErrors = genericValidation();
//        for (String jsonPath : jsonSchemPaths) {
//            List<ValidationItem> specErrors = additionalValidation(jsonPath);
//            validationErrors.addAll(specErrors);
//        }
//    }
//
//    private List<ValidationItem> additionalValidation(String jsonPath) {
//        File jsonFile = new File(jsonPath);
//        if (! jsonFile.isFile()) {
//            throw new PhenopacketValidatorRuntimeException("Could not find input json file \"" + jsonFile.getAbsolutePath() + "\"");
//        }
//        JsonSchemaValidator validator = new AdditionalJsonFileJsonSchemaValidator(phenopacket, jsonFile);
//        return validator.validate();
//    }

    public List<ValidationItem> validate(InputStream inputStream, ValidatorInfo... validations) {
        List<ValidationItem> items = new LinkedList<>();
        try {
            byte[] content = inputStream.readAllBytes();
            for (ValidatorInfo validationType : validations) {
                validatorFactory.getValidatorForType(validationType)
                        .map(validate(content))
                        .ifPresent(items::addAll);
            }
            return List.copyOf(items);

        } catch (IOException e) {
            LOGGER.warn("Error occurred during validation {}", e.getMessage(), e);
            return List.of();
        }
    }

    private static Function<PhenopacketValidator, List<ValidationItem>> validate(byte[] content) {
        return validator -> {
            try (InputStream is = new ByteArrayInputStream(content)) {
                return validator.validate(is);
            } catch (IOException e) {
                LOGGER.warn("Error occurred during validation {}", e.getMessage(), e);
                return List.of();
            }
        };
    }

//     TODO - not relevant here
//    /**
//     * Create a file object and check that the file exists
//     * @param phenopacketPath String to the input phenopacket file
//     * @return File object representing the input phenopacket
//     */
//    File initPhenopacketFile(String phenopacketPath) {
//        File f = new File(phenopacketPath);
//        if (! f.isFile()) {
//            throw new PhenopacketValidatorRuntimeException("Could not find file \"" + phenopacketPath + "\"");
//        }
//        return f;
//    }
//
//    /**
//     * Validate the Phenopacket file against the generic JSON Schema
//     */
//    private List<ValidationItem> genericValidation() {
//        JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator(phenopacket);
//        return jsonSchemaValidator.validate();
//    }
//    /**
//     * Validate the Phenopacket file for HPO-rare disease specific requirements.
//     */
//    private List<ValidationItem> hpoRareDiseaseValidation() {
//        JsonSchemaValidator hpoRareDiseaseValidator = new HpoRareDiseaseJsonSchemaValidator(phenopacket);
//        return hpoRareDiseaseValidator.validate();
//    }
//
//    public List<? extends ValidationItem> getValidationErrors() {
//        return validationErrors;
//    }
}
