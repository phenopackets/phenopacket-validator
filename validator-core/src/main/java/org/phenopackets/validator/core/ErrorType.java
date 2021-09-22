package org.phenopackets.validator.core;

import org.phenopackets.validator.core.except.PhenopacketValidatorRuntimeException;

public enum ErrorType {
    /** JSON schema error meaning that the JSON code contained a property not present in the schema. */
    JSON_ADDITIONAL_PROPERTIES("additionalProperties"),
    /** JSON schema error meaning that the JSON code failed to contain a property required by the schema. */
    JSON_REQUIRED("required"),
    /** The type of an object is not as required by the schema, e.g., we get a string instead of an array. */
    JSON_TYPE("type"),
    JSON_ENUM("todo"),
    PHENOPACKET_SUBJECT_LACKS_AGE("phenopacket subject lacks age"),
    PHENOPACKET_LACKS_SUBJECT("phenopacket lacks subject"),
    INVALID_ONTOLOGY("invalid ontology"),
    PHENOPACKET_LACKS_PHENOTYPIC_FEATURE("phenopacket lacks phenotypic feature");

    private final String name;


    ErrorType(String value) {
        this.name = value;
    }

    @Override
    public String toString() {
        return this.name;
    }


    public static ErrorType stringToErrorType(String error) {
        switch (error) {
            case "additionalProperties": return JSON_ADDITIONAL_PROPERTIES;
            case "required": return JSON_REQUIRED;
            case "type": return JSON_TYPE;
            case "enum": return JSON_TYPE;
            default:
                throw new PhenopacketValidatorRuntimeException("Did not recognize error type: \"" + error + "\"");
        }
    }
}
