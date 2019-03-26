package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RareDiseasePhenopacketValidator implements Validator<Phenopacket> {

    /**
     * Phenopacket created to represent proband in rare disease context must comply with following rules:
     * <ul>
     * <li><b>subject</b></li>
     * <ul>
     * <li>must be present</li>
     * <li>must have id</li>
     * <li>taxonomy must be defined</li>
     * <li>age string must be well formatted if present</li>
     * </ul>
     *
     * <li>at least single variant must be present</li>
     *
     * <li><b>metadata</b></li>
     * <ul>
     * <li>{@link MetaData} must not be empty</li>
     * <li>{@link MetaData} must not contain an empty {@link Resource}</li>
     * </ul>
     * </ul>
     *
     * @param pp {@link Phenopacket} to be validated
     * @return only failures are reported, so the returned list will be empty if <code>pp</code> is valid
     */
    @Override
    public List<ValidationResult> validate(Phenopacket pp) {
        List<ValidationResult> results = new ArrayList<>();

        // subject must be present and have an id
        results.add(IndividualValidators.checkThatSubjectIsNotEmpty().validate(pp));
        results.add(IndividualValidators.checkIdIsNotEmpty().validate(pp.getSubject()));
        // taxonomy must be defined
        results.add(IndividualValidators.checkTaxonomyIsNotEmpty().validate(pp.getSubject()));
        // age string must be well formatted if present
        results.add(IndividualValidators.checkAgeIfPresent().validate(pp.getSubject()));

        // metadata must not be empty and must not contain an empty resource
        results.add(MetaDataValidators.checkMetaDataNotEmpty().validate(pp.getMetaData()));
        results.addAll(MetaDataValidators.checkMetaDataHasNoEmptyResource().validate(pp.getMetaData()));

        // only return failures
        return results.stream()
                .filter(ValidationResult::notValid)
                .collect(Collectors.toList());
    }
}
