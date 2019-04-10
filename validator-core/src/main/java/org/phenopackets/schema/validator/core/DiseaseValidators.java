package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.core.Disease;
import org.phenopackets.schema.v1.core.OntologyClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Disease must comply with the following rules:
 * <ul>
 *     <li>an {@link OntologyClass} that represents the disease is <em>required</em></li>
 * </ul>
 *
 * @author Daniel Danis <daniel.danis@jax.org>
 */
public class DiseaseValidators {


    private DiseaseValidators() {
        // private no-op
    }


    /**
     * An ontology class representing the disease must not be empty.
     * <p>
     * Here we check that
     * <ul>
     *     <li>
     *         <em>term</em>
     *     <ul>
     *         <li>is present</li>
     *         <li>ID is well formatted</li>
     *         <li>label is well formatted</li>
     *     </ul>
     *     </li>
     * </ul>
     */
    public static Validator<Disease> checkTermIsPresent() {
        return d -> {
            List<ValidationResult> results = new ArrayList<>();
            OntologyClass term = d.getTerm();

            results.add(OntologyClassValidators.checkNotEmpty().validate(term));
            results.add(OntologyClassValidators.checkIdIsWellFormatted().validate(term));
            results.add(OntologyClassValidators.checkLabelIsWellFormatted().validate(term));

            return results.stream().filter(ValidationResult::notValid).collect(Collectors.toList());
        };
    }


    /**
     * Check that the {@link Disease} is not an empty instance.
     */
    public static ValidationCheck<Disease> checkNotEmpty() {
        return d -> d.equals(Disease.getDefaultInstance())
                ? ValidationResult.fail("Disease must not be empty")
                : ValidationResult.pass();
    }
}
