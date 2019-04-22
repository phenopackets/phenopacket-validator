package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Family;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Pedigree;
import org.phenopackets.schema.v1.core.Pedigree.Person;
import org.phenopackets.schema.v1.core.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FamilyValidators {

    /**
     * {@link Family} created to represent <em>proband</em> and his/her <em>relatives</em> in the rare disease context
     * must comply with the following rules:
     * <ul>
     * <li><b>Family</b>
     * <ul>
     * <li>must have <code>id</code></li>
     * <li>must have <code>proband</code></li>
     * <li><code>proband</code>, as well as <code>relatives</code> must be valid {@link Phenopacket}s by the
     * {@link PhenopacketValidators#rareDiseasePhenopacketValidator()}</li>
     * </ul>
     * </li>
     *
     * <li><b>Pedigree</b>
     * <ul>
     * <li>must be present</li>
     * <li>must be consistent:
     * <ul>
     * <li>all the {@link Individual}s (<code>proband</code>, as well as <code>relatives</code>) must be represented in the
     * pedigree</li>
     * <li>for each pedigree member a {@link Phenopacket} must exist, either as <code>proband</code>, or in the
     * <code>relatives</code> list. Pedigree member's id must match the phenopacket subject's id.</li></ul>
     * </li>
     * </ul>
     * </li>
     *
     * <li><b>Metadata</b>
     * <ul>
     * <li>{@link MetaData} must not be empty</li>
     * <li>{@link MetaData} must not contain an empty {@link Resource}</li>
     * <li><b>Resources</b>
     * <ul>
     * <li>there are no <em>unused</em> {@link Resource}s in the {@link MetaData}</li>
     * <li>there are no <em>undefined</em> {@link Resource}s in the {@link MetaData}</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     *
     * @return an empty list if the {@link Family} is valid (only failures are reported)
     */
    public static Validator<Family> rareDiseaseFamilyValidator() {
        return fml -> {
            List<ValidationResult> results = new ArrayList<>();

            // family must have id
            results.add(checkThatFamilyHasId().validate(fml));

            // family must have proband
            results.add(checkThatProbandIsNotEmpty().validate(fml));

            // proband must be a valid rare disease phenopacket
            results.addAll(PhenopacketValidators.rareDiseasePhenopacketValidator().validate(fml.getProband()));

            // all relatives must be valid rare disease phenopackets
            results.addAll(fml.getRelativesList().stream()
                    .map(PhenopacketValidators.rareDiseasePhenopacketValidator()::validate)
                    .flatMap(List::stream)
                    .collect(Collectors.toList()));

            // pedigree must be present
            results.add(FamilyValidators.checkThatPedigreeIsPresent().validate(fml));
            // proband must be represented in the pedigree
            results.add(FamilyValidators.checkThatProbandIsPresentInPedigree().validate(fml));
            // all relatives must be represented in the pedigree as well
            results.addAll(FamilyValidators.checkThatRelativesArePresentInPedigree().validate(fml));
            // and vice versa - there must be a phenopacket present for all the pedigree members
            results.addAll(FamilyValidators.checkThatPedigreeMembersInfoIsPresent().validate(fml));

            // metadata must not be empty and must not contain an empty resource
            results.add(MetaDataValidators.checkMetaDataNotEmpty().validate(fml.getMetaData()));
            results.addAll(MetaDataValidators.checkMetaDataHasNoEmptyResource().validate(fml.getMetaData()));

            // there are no unused resources
            results.addAll(ResourceValidators.checkUnusedResources().validate(fml.getProband()));
            // there are no undefined resources
            results.addAll(ResourceValidators.checkUndefinedResorces().validate(fml.getProband()));

            // returns failures only
            return results.stream()
                    .filter(ValidationResult::notValid)
                    .collect(Collectors.toList());
        };
    }


    /**
     * Check that Family#id field is not missing
     */
    public static ValidationCheck<Family> checkThatFamilyHasId() {
        return fml -> fml.getId().isEmpty()
                ? ValidationResult.fail("Family must have id")
                : ValidationResult.pass();
    }


    /**
     * Check that {@link Phenopacket} instance in the Family#proband field is not missing
     */
    public static ValidationCheck<Family> checkThatProbandIsNotEmpty() {
        return fml -> fml.getProband().equals(Phenopacket.getDefaultInstance())
                ? ValidationResult.fail("Family must have proband")
                : ValidationResult.pass();
    }

    /**
     * Check that {@link Pedigree} information has been provided
     */
    public static ValidationCheck<Family> checkThatPedigreeIsPresent() {
        return fml -> fml.getPedigree().equals(Pedigree.getDefaultInstance())
                ? ValidationResult.fail("Pedigree must be present")
                : ValidationResult.pass();
    }

    /**
     * Check that there is a record for a {@link Person} corresponding to the <code>proband</code> in {@link Pedigree}
     * of the {@link Family}
     */
    public static ValidationCheck<Family> checkThatProbandIsPresentInPedigree() {
        return fml -> {
            if (fml.getProband().equals(Phenopacket.getDefaultInstance())) {
                return ValidationResult.fail("Proband has not been defined for family");
            }
            String probandId = fml.getProband().getSubject().getId();
            return fml.getPedigree().getPersonsList().stream()
                    .anyMatch(p -> p.getIndividualId().equals(probandId))
                    ? ValidationResult.pass()
                    : ValidationResult.fail(String.format("Proband with id '%s' is not represented in pedigree", probandId));
        };
    }

    public static Validator<Family> checkThatRelativesArePresentInPedigree() {
        return fml -> {
            Set<String> personsIds = fml.getPedigree().getPersonsList().stream()
                    .map(Person::getIndividualId)
                    .collect(Collectors.toSet());

            return fml.getRelativesList().stream()
                    .filter(pp -> !personsIds.contains(pp.getSubject().getId()))
                    .map(pp -> ValidationResult.fail(
                            String.format("Relative '%s' is not present in pedigree", pp.getSubject().getId())))
                    .collect(Collectors.toList());
        };
    }

    public static Validator<Family> checkThatPedigreeMembersInfoIsPresent() {
        return fml -> {

            Set<String> ids = fml.getRelativesList().stream().map(pp -> pp.getSubject().getId()).collect(Collectors.toSet());
            ids.add(fml.getProband().getSubject().getId());

            return fml.getPedigree().getPersonsList().stream()
                    .map(Person::getIndividualId)
                    .filter(id -> !ids.contains(id))
                    .map(id -> ValidationResult.fail(String.format("Phenopacket describing pedigree member '%s' is missing", id)))
                    .collect(Collectors.toList());
        };
    }

}
