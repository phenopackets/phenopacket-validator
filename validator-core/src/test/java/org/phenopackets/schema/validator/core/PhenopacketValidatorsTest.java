package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


class PhenopacketValidatorsTest {

    @Test
    void failuresOnEmptyPhenopacket() {
        Phenopacket pp = Phenopacket.getDefaultInstance();

        List<ValidationResult> results = PhenopacketValidators.rareDiseasePhenopacketValidator().validate(pp);

        assertThat(results.size(), is(3));
        assertThat(results, hasItems(
                ValidationResult.fail("Subject must not be empty"),
                ValidationResult.fail("Individual must have an id"),
                ValidationResult.fail("Metadata is empty")
        ));
    }


    @Test
    void passWithValidPhenopacket() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();

        List<ValidationResult> results = PhenopacketValidators.rareDiseasePhenopacketValidator().validate(pp);

        assertThat(results.isEmpty(), is(true));
    }

}