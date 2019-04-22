package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PhenopacketValidators {

    private PhenopacketValidators() {
        // private no-op
    }


    /**
     * Phenopacket created to represent <em>proband</em> in rare disease context must comply with the following rules:
     * <ul>
     * <li><b>Subject</b>
     * <ul>
     * <li>must be present</li>
     * <li>must have id</li>
     * <li>taxonomy must be defined</li>
     * <li>age string must be well formatted if present</li>
     * </ul></li>
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
     * @return an empty list if the {@link Phenopacket} is valid (only failures are reported)
     */
    public static Validator<Phenopacket> rareDiseasePhenopacketValidator() {
        return pp -> {
            List<ValidationResult> results = new ArrayList<>();

            // subject must be present and have an id
            results.add(IndividualValidators.checkThatSubjectIsNotEmpty().validate(pp));
            results.add(IndividualValidators.checkIdIsNotEmpty().validate(pp.getSubject()));

            // age string must be well formatted if present
            results.add(IndividualValidators.checkAgeIfPresent().validate(pp.getSubject()));

            // metadata must not be empty and must not contain an empty resource
            results.add(MetaDataValidators.checkMetaDataNotEmpty().validate(pp.getMetaData()));
            results.addAll(MetaDataValidators.checkMetaDataHasNoEmptyResource().validate(pp.getMetaData()));
            // there are no unused resources
            results.addAll(ResourceValidators.checkUnusedResources().validate(pp));
            // there are no undefined resources
            results.addAll(ResourceValidators.checkUndefinedResorces().validate(pp));

            // returns failures only
            return results.stream()
                    .filter(ValidationResult::notValid)
                    .collect(Collectors.toList());
        };
    }
}
