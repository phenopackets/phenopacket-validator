package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.core.OntologyClass;

/**
 * Validators for {@link OntologyClass} message.
 *
 * @author Daniel Danis <daniel.danis@jax.org>
 */
public class OntologyClassValidators {

    /**
     * Pattern for term id prefix, e.g. <code>HP</code>, <code>A_B</code>.
     */
    private static final String IDENTIFIER_PATTERN = "[A-Za-z_]+";

    /**
     * Pattern for term id suffix, e.g. <code>1234567</code>, <code>C28743</code>.
     */
    private static final String SUFFIX_PATTERN = "[\\w\\d]+";

    private OntologyClassValidators() {
        // private no-op
    }

    /**
     * Basic check that fails when {@link OntologyClass} is uninitialized.
     */
    public static ValidationCheck<OntologyClass> checkNotEmpty() {
        return checkNotEmpty("Ontology class must not be empty");
    }

    /**
     * Basic check that fails when {@link OntologyClass} is uninitialized.
     *
     * @param failMessage message inserted into {@link ValidationResult} if the check fails
     */
    public static ValidationCheck<OntologyClass> checkNotEmpty(String failMessage) {
        return oc -> {
            if (oc.equals(OntologyClass.getDefaultInstance())) {
                return ValidationResult.fail(failMessage);
            }
            return ValidationResult.pass();
        };
    }

    /**
     * Identifier must meet criteria:
     * <ul>
     * <li>Id string must not be empty</li>
     * <li>Id string must contain prefix and suffix, these are separated by colon (<code>':'</code>)</li>
     * <li>Prefix of the id string matches pattern <code>'[A-Za-z_]+'</code></li>
     * <li>Suffix of the id string matches pattern <code>'\d+'</code></li>
     * </ul>
     */
    public static ValidationCheck<OntologyClass> checkIdIsWellFormatted() {
        return oc -> {
            String id = oc.getId();
            if (id.isEmpty()) {
                return ValidationResult.fail("Id string must not be empty");
            }

            int colonIdx = id.indexOf(':');
            if (colonIdx < 0) {
                return ValidationResult.fail("Missing required ':' in the id string");
            } else {
                String prefix = id.substring(0, colonIdx);
                if (!prefix.matches(IDENTIFIER_PATTERN)) {
                    // TODO - this is not very informative message for the end user, however I don't know what to put here
                    return ValidationResult.fail(String.format("Id prefix does not match '%s' pattern", IDENTIFIER_PATTERN));
                }
                String suffix = id.substring(colonIdx + 1);
                if (!suffix.matches(SUFFIX_PATTERN)) {
                    // TODO - this is not very informative message for the end user as well
                    return ValidationResult.fail(String.format("Id suffix does not match '%s' pattern", SUFFIX_PATTERN));
                }
            }
            return ValidationResult.pass();
        };
    }

    /**
     * Label must not be empty string.
     */
    public static ValidationCheck<OntologyClass> checkLabelIsWellFormatted() {
        return oc -> {
            String label = oc.getLabel();
            return label.isEmpty()
                    ? ValidationResult.fail("Label string must not be empty")
                    : ValidationResult.pass();
        };
    }

}
