package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.*;

import java.util.stream.Collectors;


/**
 * Requirements for {@link Individual}:
 * <ul>
 * <li>individual must not be empty</li>
 * <li><code>id</code> must be present</li>
 * <li><code>age</code> should be present and if it is, then it must be well formatted</li>
 * <li><code>sex</code> should be specified</li>
 * </ul>
 * <p>
 * Checks performed on {@link Phenopacket} level:
 * <ul>
 * <li>subject must not be empty</li>
 * <li>if {@link Biosample}s are present, then {@link Biosample#getIndividualId()} must match the {@link Individual#getId()}</li>
 * </ul>
 */
public class IndividualValidators {


    private IndividualValidators() {
        // private no-op
    }

    /**
     * Check that {@link Phenopacket}'s subject is not an empty {@link Individual} instance.
     */
    public static ValidationCheck<Phenopacket> checkThatSubjectIsNotEmpty() {
        return pp -> checkNotEmpty().validate(pp.getSubject()).isValid()
                ? ValidationResult.pass()
                : ValidationResult.fail("Subject must not be empty");
    }

    /**
     * Check that the {@link Individual} is not an empty instance.
     */
    public static ValidationCheck<Individual> checkNotEmpty() {
        return i -> i.equals(Individual.getDefaultInstance())
                ? ValidationResult.fail("Individual must not be empty")
                : ValidationResult.pass();
    }

    /**
     * Check that the {@link Individual} has an id.
     */
    public static ValidationCheck<Individual> checkIdIsNotEmpty() {
        return i -> i.getId().isEmpty()
                ? ValidationResult.fail("Individual must have an id")
                : ValidationResult.pass();
    }

    /**
     * {@link Age} information should be specified, otherwise a warning is emitted.
     * <p>
     *     Age must also be valid if specified.
     */
    public static ValidationCheck<Individual> checkAgeIfPresent() {
        return i -> {
            switch (i.getAgeCase()) {
                case AGE_AT_COLLECTION:
                    Age ageAtCollection = i.getAgeAtCollection();
                    return AgeValidators.ageStringIsWellFormatted().validate(ageAtCollection);
                case AGE_RANGE_AT_COLLECTION:
                    AgeRange ageRangeAtCollection = i.getAgeRangeAtCollection();
                    Age start = ageRangeAtCollection.getStart();
                    Age end = ageRangeAtCollection.getEnd();
                    ValidationResult startResult = AgeValidators.ageStringIsWellFormatted().validate(start);
                    return startResult.isValid()
                            ? AgeValidators.ageStringIsWellFormatted().validate(end)
                            : startResult;
                case AGE_NOT_SET:
                    return ValidationResult.warn("Age should be specified");
                default:
                    // This might happen if the Age element is extended in future. In that case we'd have to revisit this
                    // code anyway
                    return ValidationResult.pass();
            }
        };
    }

    /**
     * {@link Sex} information should be provided, otherwise a warning is emitted.
     */
    public static ValidationCheck<Individual> checkSexIfPresent() {
        return i -> i.getSex().equals(Sex.UNKNOWN_SEX)
                ? ValidationResult.warn("Sex should be specified")
                : ValidationResult.pass();
    }

    /**
     * If {@link Biosample}s are present, then {@link Biosample#getIndividualId()} must match the {@link Individual#getId()}.
     */
    public static Validator<Phenopacket> checkThatSubjectIdCorrespondsToBiosampleIndividualIds() {
        return pp -> {
            String subjectId = pp.getSubject().getId();
            return pp.getBiosamplesList().stream()
                    .filter(b -> !b.getIndividualId().equals(subjectId))
                    .map(b -> ValidationResult.fail(String.format("Individual id '%s' found in biosample '%s:%s' does not match subject's id '%s'",
                            b.getIndividualId(), b.getId(), b.getDatasetId(), subjectId)))
                    .collect(Collectors.toList());
        };
    }

}
