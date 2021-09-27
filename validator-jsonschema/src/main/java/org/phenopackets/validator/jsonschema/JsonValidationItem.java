package org.phenopackets.validator.jsonschema;
import com.networknt.schema.ValidationMessage;

import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidationItemType;
import org.phenopackets.validator.core.ValidatorInfo;

import java.util.Objects;

/**
 * POJO to represent errors identified by JSON Schema validation.
 * @author Peter N Robinson
 */
public final class JsonValidationItem implements ValidationItem {

    private final ValidatorInfo validatorInfo;
    private final ValidationItemType itemType;
    private final String message;

    public JsonValidationItem(ValidatorInfo validatorInfo, ValidationMessage validationMessage) {
        this.validatorInfo = validatorInfo;
        this.itemType = JsonValidationItemType.stringToErrorType(validationMessage.getType());
        this.message = validationMessage.getMessage();
    }

    @Override
    public ValidatorInfo validatorInfo() {
        return validatorInfo;
    }

    @Override
    public ValidationItemType type() {
        return this.itemType;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonValidationItem that = (JsonValidationItem) o;
        return Objects.equals(validatorInfo, that.validatorInfo) && itemType == that.itemType && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validatorInfo, itemType, message);
    }

    @Override
    public String toString() {
        return "JsonValidationError{" +
                "validatorInfo='" + validatorInfo + '\'' +
                ", errorType=" + itemType +
                ", message='" + message + '\'' +
                '}';
    }

}
