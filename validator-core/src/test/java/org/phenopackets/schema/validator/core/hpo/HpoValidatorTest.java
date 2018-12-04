package org.phenopackets.schema.validator.core.hpo;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.Individual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
class HpoValidatorTest {

    @Test
    void testValidateIndividual() {
        Individual subject = Individual.newBuilder().build();
        PhenoPacket phenoPacket = PhenoPacket.newBuilder().setSubject(subject).build();

        HpoValidator validator = new HpoValidator();
        validator.validate(phenoPacket);
        assertThat(true, equalTo(true));
    }
}