package org.phenopackets.validator.jsonschema;

import org.phenopackets.validator.core.ValidationItemType;

public class JsonValidationItemTypes {

    /**
     * JSON schema error meaning that the JSON code contained a property not present in the schema.
     */
    public final static ValidationItemType JSON_ADDITIONAL_PROPERTIES = ValidationItemType.of("additionalProperties", "Use of a property not present in the schema");

    /**
     * JSON schema error meaning that the JSON code failed to contain a property required by the schema.
     */
    public final static ValidationItemType JSON_REQUIRED = ValidationItemType.of("required", "failure to contain a property required by the schema");

    /**
     * The type of an object is not as required by the schema, e.g., we get a string instead of an array.
     */
    public final static ValidationItemType JSON_TYPE = ValidationItemType.of("type", "incorrect data type of object");

    // TODO - add item description
    public final static ValidationItemType JSON_ENUM = ValidationItemType.of("enum", "TODO");

    private JsonValidationItemTypes() {}
}
