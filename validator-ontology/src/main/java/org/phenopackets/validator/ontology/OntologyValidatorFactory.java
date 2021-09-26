package org.phenopackets.validator.ontology;

import org.phenopackets.validator.core.PhenopacketValidator;
import org.phenopackets.validator.core.PhenopacketValidatorFactory;
import org.phenopackets.validator.core.ValidatorInfo;
import org.phenopackets.validator.core.except.PhenopacketValidatorRuntimeException;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OntologyValidatorFactory implements PhenopacketValidatorFactory {

    private final Map<ValidatorInfo, OntologyValidator> validatorMap;

    private static final String ONTOLOGY_VALIDATOR_TYPE = "Semantic validation";

    public OntologyValidatorFactory(File... ontologyJsonFiles) {
        validatorMap = new HashMap<>();
        for (var jsonFile: ontologyJsonFiles) {

        }
    }


    @Override
    public Optional<? extends PhenopacketValidator> getValidatorForType(ValidatorInfo type) {
        if (validatorMap.containsKey(type)) {
            return Optional.of(validatorMap.get(type));
        }
        return Optional.empty();
    }
}
