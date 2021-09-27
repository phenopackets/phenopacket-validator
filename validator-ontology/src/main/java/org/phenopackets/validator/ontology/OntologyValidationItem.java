package org.phenopackets.validator.ontology;

import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidationItemType;
import org.phenopackets.validator.core.ValidatorInfo;

import static org.phenopackets.validator.ontology.OntologyValidationItemType.ONTOLOGY_INVALID_ID;
import static org.phenopackets.validator.ontology.OntologyValidationItemType.ONTOLOGY_TERM_WITH_ALTERNATE_ID;

public class OntologyValidationItem implements ValidationItem {

    private final ValidatorInfo validatorInfo;
    private final ValidationItemType itemType;
    private final String message;

    public OntologyValidationItem(ValidatorInfo validatorInfo, ValidationItemType errorType, String validationMessage) {
        this.validatorInfo = validatorInfo;
        this.itemType = errorType;
        this.message = validationMessage;
    }


    @Override
    public ValidatorInfo validatorInfo() {
        return validatorInfo;
    }

    @Override
    public ValidationItemType type() {
        return itemType;
    }

    @Override
    public String message() {
        return message;
    }

    public static OntologyValidationItem invalidTermId(ValidatorInfo validatorInfo,String validationMessage) {
        return new OntologyValidationItem(validatorInfo, ONTOLOGY_INVALID_ID, validationMessage);
    }

    public static OntologyValidationItem alternateId(ValidatorInfo validatorInfo,String validationMessage) {
        return new OntologyValidationItem(validatorInfo, ONTOLOGY_TERM_WITH_ALTERNATE_ID, validationMessage);
    }


}
