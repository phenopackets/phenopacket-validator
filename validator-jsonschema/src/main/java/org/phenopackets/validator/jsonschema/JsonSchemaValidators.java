package org.phenopackets.validator.jsonschema;

import org.phenopackets.validator.core.PhenopacketValidator;
import org.phenopackets.validator.core.ValidatorInfo;
import org.phenopackets.validator.core.except.PhenopacketValidatorRuntimeException;

import java.io.InputStream;
import java.util.Map;

/**
 * Utility class that uses JSON schema definitions that are bundled within the application (on classpath).
 * <p>
 * @author Daniel Danis
 * @author Peter N Robinson
 */
public class JsonSchemaValidators {

    public static Map<ValidatorInfo, PhenopacketValidator> genericValidator() {
        return Map.of(
                ValidatorInfo.generic(), makeJsonValidator("/schema/phenopacket-generic.json", ValidatorInfo.generic())
        );
    }

    public static Map<ValidatorInfo, PhenopacketValidator>  rareHpoValidator() {
        return Map.of(
                ValidatorInfo.generic(), makeJsonValidator("/schema/hpo-rare-disease-schema.json", ValidatorInfo.generic())
        );
    }

    private static PhenopacketValidator makeJsonValidator(String schemaPath, ValidatorInfo validationName) {
        InputStream inputStream = JsonSchemaValidators.class.getResourceAsStream(schemaPath);
        if (inputStream == null)
            throw new PhenopacketValidatorRuntimeException("Invalid JSON schema path `" + schemaPath + '`');

        return JsonSchemaValidator.of(inputStream, validationName);
    }

    private JsonSchemaValidators() {}

}
