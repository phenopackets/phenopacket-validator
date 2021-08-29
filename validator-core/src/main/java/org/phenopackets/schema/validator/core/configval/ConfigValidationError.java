package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.validator.core.validation.ErrorType;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.util.Objects;


/**
 * POJO to represent error identified by violation of configuration file.
 * @author Peter Robinson
 */
public class ConfigValidationError implements ValidationItem {


    private final ErrorType errorType;
    private final String message;

    public ConfigValidationError(ErrorType errorType, String message) {
        this.errorType = errorType;
        this.message = message;
    }


    @Override
    public ErrorType errorType() {
        return null;
    }

    @Override
    public String message() {
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorType, message);
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof ConfigValidationError)) return false;
        ConfigValidationError that = (ConfigValidationError) obj;
        return this.message.equals(that.message) && this.errorType.equals(that.errorType);
    }
}
