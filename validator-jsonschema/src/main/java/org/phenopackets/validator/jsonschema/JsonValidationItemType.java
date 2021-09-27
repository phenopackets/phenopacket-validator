package org.phenopackets.validator.jsonschema;

import org.phenopackets.validator.core.ValidationItemType;
import org.phenopackets.validator.core.except.PhenopacketValidatorRuntimeException;

public class JsonValidationItemType implements ValidationItemType {

    private final String itemType;
    private final String description;

    /**
     * JSON schema error meaning that the JSON code contained a property not present in the schema.
     */
    public final static JsonValidationItemType JSON_ADDITIONAL_PROPERTIES =
            new JsonValidationItemType("additionalProperties", "Use of a property not present in the schema");
    /** JSON schema error meaning that the JSON code failed to contain a property required by the schema. */
    public final static JsonValidationItemType JSON_REQUIRED =
            new JsonValidationItemType("required", "failure to contain a property required by the schema");
    /** The type of an object is not as required by the schema, e.g., we get a string instead of an array. */
    public final static JsonValidationItemType JSON_TYPE =
            new JsonValidationItemType("type", "incorrect data type of object");
    public final static JsonValidationItemType JSON_ENUM=
            new JsonValidationItemType("enum", "TODO");





    public JsonValidationItemType(String type, String desc) {
        itemType = type;
        description = desc;
    }

    @Override
    public String itemType() {
        return itemType;
    }

    @Override
    public String itemDescription() {
        return description;
    }

    public static JsonValidationItemType stringToErrorType(String error) {
        switch (error) {
            case "additionalProperties": return JSON_ADDITIONAL_PROPERTIES;
            case "required": return JSON_REQUIRED;
            case "type": return JSON_TYPE;
            case "enum": return JSON_TYPE;
            default:
                throw new PhenopacketValidatorRuntimeException("Did not recognize JSON error type: \"" + error + "\"");
        }
    }
}
