package org.phenopackets.validator.core;

/**
 * The interface represents a single issue found during validation.
 * <p>
 * @author Daniel Danis
 * @author Peter N Robinson
 */
public interface ValidationItem {

    static ValidationItem of(ValidatorInfo validatorInfo, ValidationItemType type, String message) {
        return new ValidationItemDefault(validatorInfo, type, message);
    }

    /**
     * @return basic description of the validator that produced this issue.
     */
    ValidatorInfo validatorInfo();

    /**
     * @return description of the issue in a standard way intended for both grouping of the issues and human consumption.
     */
    ValidationItemType type();

    /**
     * @return string with description of the issue intended for human consumption.
     */
    String message();

}
