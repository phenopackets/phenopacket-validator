package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.core.Evidence;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EvidenceValidatorsTest {


    @Test
    void failOnEmpty() {
        Evidence e = Evidence.getDefaultInstance();

        ValidationResult result = EvidenceValidators.checkNotEmpty().validate(e);

        assertThat(result, is(ValidationResult.fail("Evidence must not be empty")));
    }

    @Test
    void passOnNonEmpty() {
        Evidence e = TestExamples.getEvidenceInstance();

        ValidationResult result = EvidenceValidators.checkNotEmpty().validate(e);

        assertTrue(result.isValid());
    }

    @Test
    void failOnMissingEvidenceCode() {
        Evidence e = Evidence.getDefaultInstance();

        List<ValidationResult> results = EvidenceValidators.checkEvidenceCodeIsPresent().validate(e);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Evidence code must not be empty")));
    }

    @Test
    void failOnMisformattedEvidenceCode() {
        Evidence e = Evidence.newBuilder().setEvidenceCode(OntologyClass.newBuilder()
                .setId("HP;1234567")
                .setLabel("")
                .build()).build();

        List<ValidationResult> results = EvidenceValidators.checkEvidenceCodeIsPresent().validate(e);

        assertThat(results.size(), is(2));
        assertThat(results, hasItem(ValidationResult.fail("Missing required ':' in the id string")));
        assertThat(results, hasItem(ValidationResult.fail("Label string must not be empty")));
    }

    @Test
    void passOnPresentEvidenceCode() {
        Evidence e = TestExamples.getEvidenceInstance();

        List<ValidationResult> results = EvidenceValidators.checkEvidenceCodeIsPresent().validate(e);

        assertThat(results.size(), is(0));
    }
}