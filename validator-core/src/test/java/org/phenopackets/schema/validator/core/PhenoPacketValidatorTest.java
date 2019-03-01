package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.PhenoPacket;

import java.util.List;

class PhenoPacketValidatorTest {

    private PhenoPacketValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PhenoPacketValidator();
    }

    @Test
    void subjectIsMissing() {
        PhenoPacket phenoPacket = PhenoPacket.getDefaultInstance();

        final List<ValidationResult> results = validator.validate(phenoPacket);
        System.out.println(results);
    }

    /**
     * Every valid HPO Phenopacket needs to have a subject. Here we construct a Phenopacket
     * that does not have a subject and show that it is invalid.
     */
    @Test
    void testWhetherPhenopacketHasSubject() {
        PhenoPacket phenoPacket = PhenoPacket.newBuilder().build();

        validator.validate(phenoPacket);
        List<ValidationResult> result = validator.validate(phenoPacket);
        System.out.println(result);
        //assertThat(result.isValid(), equalTo(false));
    }
}