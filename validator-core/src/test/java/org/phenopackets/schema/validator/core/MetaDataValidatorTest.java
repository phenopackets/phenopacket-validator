package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MetaDataValidatorTest {

    private MetaDataValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MetaDataValidator();
    }

    /**
     * Every valid HPO Phenopacket needs to have a MetaData section. Here we construct a Phenopacket
     * that does not have a MetaData section and show that it is invalid.
     */
    @Test
    void testWhetherPhenopacketContainsMetadata() {
        // TODO - complete

//        validator.validate(phenoPacket);
//        List<ValidationResult> result = validator.validate(phenoPacket);
//        System.out.println(result);
//        assertThat(result.isValid(), equalTo(false));
    }


}