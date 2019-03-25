package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * For checks relevant to top-level PhenoPacket concepts.
 * <p>
 * Check that:
 * <ul>
 * <li>subject is present</li>
 * </ul>
 */
public class PhenopacketValidator implements Validator<Phenopacket> {

    private final MetaDataValidators metaDataValidator;

//    private final OntologyResolver ontologyResolver;

    public PhenopacketValidator() {
        metaDataValidator = new MetaDataValidators();
//        ontologyResolver = new OntologyResolver(resourceDir);
    }

    /**
     * A valid phenopacket must have a subject.
     */
    private List<ValidationResult> checkExistenceOfSubject(Phenopacket phenoPacket) {

        Individual subject = phenoPacket.getSubject();
        if (subject.equals(Individual.getDefaultInstance())) {
            return Collections.singletonList(ValidationResult.fail("Phenopacket does not have a subject"));
        }
        return Collections.emptyList();
    }

    @Override
    public List<ValidationResult> validate(Phenopacket phenoPacket) {
        List<ValidationResult> results = new ArrayList<>();

        MetaData metaData = phenoPacket.getMetaData();
        List<Resource> resources = metaData.getResourcesList();
        for (Resource resource : resources) {
//            resource.getUrl()
        }

        results.addAll(checkExistenceOfSubject(phenoPacket));

        return results;
    }
}
