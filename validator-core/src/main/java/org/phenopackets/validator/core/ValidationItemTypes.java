package org.phenopackets.validator.core;

public class ValidationItemTypes {

    private static final ValidationItemType SYNTAX_ERROR = ValidationItemType.of("Syntax error", "The input does not conform the with phenopacket syntax");

    private ValidationItemTypes() {}

    public static ValidationItemType syntaxError() {
        return SYNTAX_ERROR;
    }

}
