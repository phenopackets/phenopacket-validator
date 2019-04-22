package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.core.Gene;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

class GeneValidatorsTest {


    @Test
    void failsWhenGeneIsEmpty() {
        Gene g = Gene.getDefaultInstance();
        ValidationResult result = GeneValidators.checkNotEmpty().validate(g);
        assertThat(result, is(ValidationResult.fail("Gene must not be empty")));
    }

    @Test
    void passesWhenGeneIsNotEmpty() {
        Gene g = TestExamples.getSURF1Gene();
        ValidationResult result = GeneValidators.checkNotEmpty().validate(g);
        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failsWhenGeneIdIsEmpty() {
        Gene g = Gene.getDefaultInstance();
        ValidationResult result = GeneValidators.checkIdIsPresent().validate(g);
        assertThat(result, is(ValidationResult.fail("Gene id must not be empty")));
    }

    @Test
    void passesWhenGeneIdIsNotEmpty() {
        Gene g = TestExamples.getSURF1Gene();
        ValidationResult result = GeneValidators.checkIdIsPresent().validate(g);
        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failsWhenGeneSymbolIsEmpty() {
        Gene g = Gene.getDefaultInstance();
        ValidationResult result = GeneValidators.checkSymbolIsPresent().validate(g);
        assertThat(result, is(ValidationResult.fail("Gene symbol must not be empty")));
    }

    @Test
    void passesWhenGeneSymbolIsNotEmpty() {
        Gene g = TestExamples.getSURF1Gene();
        ValidationResult result = GeneValidators.checkSymbolIsPresent().validate(g);
        assertThat(result, is(ValidationResult.pass()));
    }
}