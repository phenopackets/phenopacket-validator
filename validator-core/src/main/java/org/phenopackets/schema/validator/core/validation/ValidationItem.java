package org.phenopackets.schema.validator.core.validation;

public interface ValidationItem {

    ErrorType errorType();
    String message();

}
