package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

class RareDiseasePhenopacketValidatorTest {

    private RareDiseasePhenopacketValidator instance;

    @BeforeEach
    void setUp() {
        instance = new RareDiseasePhenopacketValidator();
    }

    @Test
    void failOnEverything() {
        Phenopacket pp = Phenopacket.getDefaultInstance();

        List<ValidationResult> results = instance.validate(pp);

        results.forEach(System.err::println);
        assertThat(results.size(), is(4));
        assertThat(results, hasItems(
                ValidationResult.fail("Subject must not be empty"),
                ValidationResult.fail("Individual must have an id"),
                ValidationResult.fail("Individual's taxonomy info must be present"),
                ValidationResult.fail("Metadata is empty")
        ));
    }
}