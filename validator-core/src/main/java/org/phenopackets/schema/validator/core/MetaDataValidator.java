package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.MetaData;

import java.util.ArrayList;
import java.util.List;

public class MetaDataValidator implements Validator<PhenoPacket> {


    /**
     * @return
     */
    private ValidationCheck<MetaData> checkMetaData() {
        return metadata -> {
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
            return ValidationResult.fail("Check metadata is always failing for now");
        };
    }

    private List<ValidationResult> checkMetaDataNotEmpty(PhenoPacket phenoPacket) {
        List<ValidationResult> results = new ArrayList<>();

        if (phenoPacket.getMetaData().equals(MetaData.getDefaultInstance())) {
            results.add(ValidationResult.fail("Metadata is empty"));
        }

        return results;
    }

    @Override
    public List<ValidationResult> validate(PhenoPacket message) {
        return null;
    }
}
