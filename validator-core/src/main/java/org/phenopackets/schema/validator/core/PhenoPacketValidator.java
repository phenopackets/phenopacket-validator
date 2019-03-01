package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.Individual;

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
public class PhenoPacketValidator implements Validator<PhenoPacket> {


    /**
     * A valid phenopacket must have a subject.
     */
    private List<ValidationResult> checkExistenceOfSubject(PhenoPacket message) {

        Individual subject = message.getSubject();
        if (subject.equals(Individual.getDefaultInstance())) {
            return Collections.singletonList(ValidationResult.fail("Phenopacket does not have a subject"));
        }
        return Collections.emptyList();

    }

    @Override
    public List<ValidationResult> validate(PhenoPacket message) {
        List<ValidationResult> results = new ArrayList<>();

        results.addAll(checkExistenceOfSubject(message));

        return results;
    }
}
