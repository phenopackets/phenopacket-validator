package org.phenopackets.schema.validator.core.jsonschema;
import com.networknt.schema.ValidationMessage;

import java.util.Objects;

public class ValidationError {

    private final String type;
    private final String message;

    public ValidationError(ValidationMessage validationMessage) {
        this.type = validationMessage.getType();
        this.message = validationMessage.getMessage();
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof ValidationError)) return false;
        ValidationError that = (ValidationError) obj;
        return this.message.equals(that.message) && this.type.equals(that.type);
    }
}
