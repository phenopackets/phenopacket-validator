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
     * @return
     */
    public List<String> getFields() {
        return List.of(error.errorType().name(), error.message(), error.validatorInfo().validatorId());
    }
}
