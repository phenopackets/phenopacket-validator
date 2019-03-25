package org.phenopackets.schema.validator.core;

import com.google.protobuf.Timestamp;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Resource;

import java.util.stream.Collectors;


/**
 * Provide validators and validation checks for attributes of {@link MetaData}:
 * <ul>
 * <li>{@link MetaData} is not empty</li>
 * <li><code>created</code> {@link com.google.protobuf.Timestamp} is present</li>
 * <li><code>created_by</code> is not empty</li>
 * <li><code>submitted_by</code> is not empty</li>
 * <li>{@link MetaData} does not contain uninitialized {@link Resource}</li>
 * </ul>
 *
 * @author Daniel Danis <daniel.danis@jax.org>
 */
public class MetaDataValidators {


    private MetaDataValidators() {
        // private no-op
    }

    /**
     * Check that {@link MetaData} is not not empty.
     */
    public static ValidationCheck<MetaData> checkMetaDataNotEmpty() {
        return md -> md.equals(MetaData.getDefaultInstance())
                ? ValidationResult.fail("Metadata is empty")
                : ValidationResult.pass();
    }

    public static ValidationCheck<MetaData> checkCreatedIsNotEmpty() {
        return md -> md.getCreated().equals(Timestamp.getDefaultInstance())
                ? ValidationResult.fail("Missing timestamp for when this phenopacket was created (field 'created')")
                : ValidationResult.pass();
    }

    /**
     * Check field describing contributor / program that created the Phenopacket is not empty.
     */
    public static ValidationCheck<MetaData> checkCreatedByIsNotEmpty() {
        return md -> md.getCreatedBy().isEmpty()
                ? ValidationResult.fail("Missing author information (field 'created_by')")
                : ValidationResult.pass();
    }

    /**
     * Check field describing Phenopacket submitter is not empty.
     */
    public static ValidationCheck<MetaData> checkSubmittedByIsNotEmpty() {
        return md -> md.getSubmittedBy().isEmpty()
                ? ValidationResult.fail("Missing information about the person/organisation/network that has submitted the phenopacket (field 'submitted_by')")
                : ValidationResult.pass();
    }

    /**
     * Check that {@link MetaData} does not contain uninitialized resource
     */
    public static Validator<MetaData> checkMetaDataHasNoEmptyResource() {
        return md -> md.getResourcesList().stream()
                .map(ResourceValidators.checkResourceIsNotEmpty()::validate)
                .filter(ValidationResult::notValid)
                .collect(Collectors.toList());
    }

//    ValidationCheck<MetaData> checkMetaData() {
//        return metadata -> {
            /*
            MetaData metaData = phenoPacket.getMetaData();
            ImmutableList.Builder<ValidationResult> validationResults = ImmutableList.builder();

            validationResults.add(checkMetaDataNotEmpty(metaData));


            // these atomic and independent checks might be well represented as an independent package of ValidationCheck<T>
            // classes which can be simply tested and easily composed by other validators e.g. the checkMetaDataNotEmpty()
            // but I think we need to do a bit more first to see what makes most sense.

            for (Resource resource : metaData.getResourcesList()) {
                // check fields are not blank or better check contents e.g.
                // iriPrefix should follow a properly formed IRI pattern and end in a '_'
                // namespacePrefix should be present and match the characters of iriPrefix.substring(((iriPrefix.length() - 2) - namespacePrefix.length()), iriPrefix.length() - 2)
                // "namespacePrefix": "HP",
                // "iriPrefix": "http://purl.obolibrary.org/obo/HP_"

                // check each resource has at least one CURIE present in an OntologyClass, otherwise report '"Unused resource " + resource.getId()'
            }
            // check the reverse - i.e. all CURIEs in all OntologyClasses are represented by a Resource

            return validationResults.build();
            */
//            return ValidationResult.fail("Check metadata is always failing for now");
//        };
//    }

}
