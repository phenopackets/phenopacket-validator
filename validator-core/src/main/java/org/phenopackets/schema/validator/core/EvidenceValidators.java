package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.core.Evidence;
import org.phenopackets.schema.v1.core.OntologyClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Requirements for {@link Evidence}:
 * <ul>
 * <li><code>evidence_code</code> must not be empty and {@link OntologyClass} representing the code must be well formatted</li>
 * </ul>
 */
public class EvidenceValidators {

    private EvidenceValidators() {
        // private no-op
    }


    /**
     * Check that the {@link Evidence} is not an empty instance.
     */
    public static ValidationCheck<Evidence> checkNotEmpty() {
        return e -> e.equals(Evidence.getDefaultInstance())
                ? ValidationResult.fail("Evidence must not be empty")
                : ValidationResult.pass();
    }

    /**
     * An ontology class representing the evidence code must not be empty.
     * <p>
     * Here we check that
     * <ul>
     *     <li>
     *         <em>evidence code</em>
     *     <ul>
     *         <li>is present</li>
     *         <li>ID is well formatted</li>
     *         <li>label is well formatted</li>
     *     </ul>
     *     </li>
     * </ul>
     */
    public static Validator<Evidence> checkEvidenceCodeIsPresent() {
        return e -> {
            List<ValidationResult> results = new ArrayList<>(3);
            OntologyClass evidenceCode = e.getEvidenceCode();
            ValidationResult empty = OntologyClassValidators.checkNotEmpty("Evidence code must not be empty").validate(evidenceCode);
            if (empty.notValid()) {
                results.add(empty);
                return results;
            }

            results.add(OntologyClassValidators.checkIdIsWellFormatted().validate(evidenceCode));
            results.add(OntologyClassValidators.checkLabelIsWellFormatted().validate(evidenceCode));

            return results.stream().filter(ValidationResult::notValid).collect(Collectors.toList());
        };
    }
}
