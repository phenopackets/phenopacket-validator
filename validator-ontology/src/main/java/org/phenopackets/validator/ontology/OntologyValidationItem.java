package org.phenopackets.validator.ontology;

import org.phenopackets.validator.core.ErrorType;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;

public class OntologyValidationItem implements ValidationItem {

    private final ValidatorInfo validatorInfo;
    private final ErrorType errorType;
    private final String message;

    public OntologyValidationItem(ValidatorInfo validatorInfo, ErrorType errorType, String validationMessage) {
        this.validatorInfo = validatorInfo;
        this.errorType = errorType;
        this.message = validationMessage;
    }


    @Override
    public ValidatorInfo validatorInfo() {
        return validatorInfo;
    }

    @Override
    public ErrorType errorType() {
        return errorType;
    }

    @Override
    public String message() {
        return message;
    }

    public static OntologyValidationItem invalidTermId(ValidatorInfo validatorInfo,String validationMessage) {
        return new OntologyValidationItem(validatorInfo, ErrorType.ONTOLOGY_INVALID_ID, validationMessage);
    }

    public static OntologyValidationItem alternateId(ValidatorInfo validatorInfo,String validationMessage) {
        return new OntologyValidationItem(validatorInfo, ErrorType.ONTOLOGY_TERM_WITH_ALTERNATE_ID, validationMessage);
    }


}
