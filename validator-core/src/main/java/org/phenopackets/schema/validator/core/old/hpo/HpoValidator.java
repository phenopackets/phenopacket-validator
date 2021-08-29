package org.phenopackets.schema.validator.core.old.hpo;
/*
import com.google.common.collect.ImmutableList;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Resource;
import org.phenopackets.schema.validator.core.old.ValidationResult;
import org.phenopackets.schema.validator.core.old.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Class for validating HPO terms in a {@link org.phenopackets.schema.v1.PhenoPacket}.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>

@Deprecated
public class HpoValidator implements Validator<PhenoPacket> {

    private static final Logger logger = LoggerFactory.getLogger(HpoValidator.class);

    public HpoValidator() {
//        this.ontology=ontology;
    }


    @Override
    public List<ValidationResult> validate(PhenoPacket phenoPacket) {
        logger.info("validating HPO terms in phenopacket...");

        // TODO: to what extent should this be HPO specific and where should a more general validation occur?
        // Things like the MetaData validation ought to be general, yet the HPO-specific steps might be to check the
        // validity of individual curies and labels in the OntologyClasses with CURIEs with an HP prefix.

        List<Validator<PhenoPacket>> validationChecks = new ArrayList<>();
        validationChecks.add(checkMetadata());
        validationChecks.add(alwaysFail());

        return validationChecks.stream()
                .flatMap(phenoPacketValidator -> phenoPacketValidator.validate(phenoPacket).stream())
                .filter(ValidationResult::notValid)
                .collect(toList());
    }

    private Validator<PhenoPacket> checkMetadata() {
        return phenoPacket -> {
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
        };
    }

    private ValidationResult checkMetaDataNotEmpty(MetaData metaData) {
        return metaData.equals(MetaData.getDefaultInstance()) ? ValidationResult.fail("Metadata is empty") : ValidationResult
                .pass();
    }

    private Validator<PhenoPacket> alwaysFail() {
        return it -> ImmutableList.of(ValidationResult.fail("Always fails"));
    }

}
*/