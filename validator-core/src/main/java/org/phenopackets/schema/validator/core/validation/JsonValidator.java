package org.phenopackets.schema.validator.core.validation;

import java.util.List;

/**
 * Common interface for validation by JSON schema.
 * @author Peter N Robinson
 */
public interface JsonValidator {

    List<ValidationItem> validate();

}
