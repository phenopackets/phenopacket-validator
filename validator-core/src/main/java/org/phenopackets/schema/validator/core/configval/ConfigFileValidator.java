package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.util.List;

public interface ConfigFileValidator {
    public List<ValidationItem> validate();
}
