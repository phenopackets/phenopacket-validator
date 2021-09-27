package org.phenopackets.validator.cli.results;

import org.phenopackets.validator.core.ValidationItem;

import java.util.List;

/**
 * Visualizer for validationItems
 */
public class ValidationItemTsvVisualizer {
    private final ValidationItem error;
    public ValidationItemTsvVisualizer(ValidationItem item) {
        this.error = item;
    }

    /**
     * Get a list of fields for display
     * @return A list of Strings that can be used to create a row of a TSV or CSV file
     */
    public List<String> getFields() {
        return List.of(error.type().itemType(), error.message(), error.validatorInfo().validatorId());
    }
}
