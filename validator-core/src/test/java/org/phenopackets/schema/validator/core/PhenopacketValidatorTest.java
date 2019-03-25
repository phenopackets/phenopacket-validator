package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;

import java.util.List;

class PhenopacketValidatorTest {

    private PhenopacketValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PhenopacketValidator();
    }

    @Test
    void subjectIsMissing() {
        Phenopacket phenoPacket = Phenopacket.getDefaultInstance();

        final List<ValidationResult> results = validator.validate(phenoPacket);
        System.out.println(results);
    }

    /**
     * Every valid HPO Phenopacket needs to have a subject. Here we construct a Phenopacket
     * that does not have a subject and show that it is invalid.
     */
    @Test
    void testWhetherPhenopacketHasSubject() {
        Phenopacket phenoPacket = Phenopacket.newBuilder().build();

        validator.validate(phenoPacket);
        List<ValidationResult> result = validator.validate(phenoPacket);
        System.out.println(result);
        //assertThat(result.isValid(), equalTo(false));
    }
}