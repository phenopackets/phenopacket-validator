package org.phenopackets.schema.validator.core.hpo;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.OntologyClass;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * This class includes some convenience functions for making valid and invalid Phenopackets that we will use to
 * test the validation machinery.
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 * @author Peter Robinson <peter.robinson@jax.org>
 */
class HpoValidatorTest {
    @Mock
    HpoOntology ontology;

    /** convenience function for creating an OntologyClass object. */
    private static OntologyClass ontologyClass(String id, String label) {
        return OntologyClass.newBuilder()
                .setId(id)
                .setLabel(label)
                .build();
    }

    @Test
    void testWhetherPhenopacketContainsMetadata() {
        Individual subject = Individual.newBuilder().build();
        PhenoPacket phenoPacket = PhenoPacket.newBuilder().setSubject(subject).build();

        HpoValidator validator = new HpoValidator(ontology);
        validator.validate(phenoPacket);
        assertFalse(validator.isValid());
    }
}