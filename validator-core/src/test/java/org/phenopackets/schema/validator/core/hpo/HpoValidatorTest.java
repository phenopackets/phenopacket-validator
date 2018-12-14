package org.phenopackets.schema.validator.core.hpo;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.validator.core.ValidationResult;

import java.util.List;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
class HpoValidatorTest {

    @Test
    void testValidateIndividual() {
        Individual subject = Individual.newBuilder().build();
        PhenoPacket phenoPacket = PhenoPacket.newBuilder()
                .setSubject(subject)
                .build();

        HpoValidator validator = new HpoValidator();
        List<ValidationResult> result = validator.validate(phenoPacket);
        System.out.println(result);
//        assertThat(result.isValid(), equalTo(false));
    }
}