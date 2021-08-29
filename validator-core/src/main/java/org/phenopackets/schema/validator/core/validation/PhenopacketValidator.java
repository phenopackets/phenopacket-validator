package org.phenopackets.schema.validator.core.validation;

import java.util.List;

/**
 * Common interface for validation by JSON schema or by configuration.
 * @author Peter N Robinson
 */
public interface PhenopacketValidator {

    public List<ValidationItem> validate();

}
