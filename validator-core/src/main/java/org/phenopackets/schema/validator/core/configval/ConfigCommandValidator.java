package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.validator.core.validation.ErrorType;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.util.ArrayList;
import java.util.List;

import static org.phenopackets.schema.validator.core.configval.ConfigAction.SPECIFY_ONTOLOGY;

public class ConfigCommandValidator {

    private final Phenopacket phenopacket;

    private final List<ValidationItem> errors;

    public ConfigCommandValidator(Phenopacket ppacket) {
        phenopacket = ppacket;
        errors = new ArrayList<>();
    }

    List<ValidationItem> validate(List<ConfigCommand> commandList) {
        for (var command : commandList) {
            switch (command.getSubject()) {
                case PHENOPACKET_SUBJECT:
                    validateSubject(command);
                    break;
                case PHENOTYPIC_SUBJECT_TIME_AT_LAST_ENCOUNTER:
                    validateSubjectTimeAtLastEncounter(command);
                    break;
                case PHENOPACKET_PHENOTYPIC_FEATURES_TYPE:
                    validatePhenotypeFeatures(command);
                    break;
            }
        }


        return errors;
    }

    /**
     * This function can check whether a phenotypicFeature is required but not present or if any of the ontology
     * terms used has an invalid ontology prefix
     * @param command
     */
    private void validatePhenotypeFeatures(ConfigCommand command) {
        if (command.getAction().equals(ConfigAction.REQUIRED)
                ||
                command.getAction().equals(ConfigAction.AT_LEAST_ONE)){
            if (phenopacket.getPhenotypicFeaturesCount() == 0) {
                errors.add(ConfigValidationError.phenopacketLacksPhenotypicFeature());
            }
        } else if (command.getAction().equals(SPECIFY_ONTOLOGY)) {
            String requiredOntology = command.getPayload();
            for (var pf : phenopacket.getPhenotypicFeaturesList()) {
                var termId = pf.getType().getId();
                // assumption -- valid ontology terms are CURIEs
                String [] fields = termId.split("[:_]");
                if (fields.length != 2) {
                    String errorMsg = String.format("Invalid CURIE - %s with split into %d fields.", termId, fields.length);
                    errors.add(new ConfigValidationError(ErrorType.INVALID_ONTOLOGY, errorMsg));
                } else {
                    String prefix = fields[0];
                    if (! prefix.equals(requiredOntology)) {
                        String errorMsg = String.format("Invalid Ontology - \"%s\" (required: \"%s\").", prefix, requiredOntology);
                        errors.add(new ConfigValidationError(ErrorType.INVALID_ONTOLOGY, errorMsg));
                    }
                }
            }
        }
    }

    private void validateSubject(ConfigCommand command) {
        if (command.getAction().equals(ConfigAction.REQUIRED)) {
            if (! phenopacket.hasSubject()) {
                errors.add(ConfigValidationError.phenopacketSubjectLacksAge());
            }
        }
    }

    /**
     * If the phenopacket has a subject, then we require it to have a timeAtLastEncounter subelement (age/time of onset).
     * @param command
     */
    private void validateSubjectTimeAtLastEncounter(ConfigCommand command) {
        if (command.getAction().equals(ConfigAction.REQUIRED) ||
                phenopacket.hasSubject() ||
                    ! phenopacket.getSubject().hasTimeAtLastEncounter() ) {
                errors.add(ConfigValidationError.phenopacketSubjectLacksAge());
        }
    }


    public List<ValidationItem> getErrors() {
        return errors;
    }
}
