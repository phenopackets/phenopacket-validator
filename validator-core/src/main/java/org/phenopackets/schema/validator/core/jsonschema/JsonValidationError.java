package org.phenopackets.schema.validator.core.jsonschema;
import com.networknt.schema.ValidationMessage;
import org.phenopackets.schema.validator.core.validation.ErrorType;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.util.Objects;

/**
 * POJO to represent errors identified by JSON Schema validation.
 * @author Peter N Robinson
 */
public final class JsonValidationError implements ValidationItem {

    private final ErrorType errorType;
    private final String message;

    public JsonValidationError(ValidationMessage validationMessage) {
        this.errorType = ErrorType.stringToErrorType(validationMessage.getType());
        this.message = validationMessage.getMessage();
    }


    @Override
    public String message() {
        return message;
    }

    @Override
    public ErrorType errorType() {
        return this.errorType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, errorType);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof JsonValidationError)) return false;
        JsonValidationError that = (JsonValidationError) obj;
        return this.message.equals(that.message) && this.errorType.equals(that.errorType);
    }

}
