package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Daniel Danis <daniel.danis@jax.org>
 */
class OntologyClassValidatorsTest {


    //    ------------------------ PASSES -----------------------------------------------

    @Test
    void passWhenOntologyClassIsWellFormatted() {
        ValidationResult result = OntologyClassValidators.checkIdIsWellFormatted().validate(TestExamples.MALE);
        assertThat(result, is(ValidationResult.pass()));
    }


    @Test
    void passWhenOntologyClassNotEmpty() {
        OntologyClass male = TestExamples.MALE;
        ValidationResult result = OntologyClassValidators.checkNotEmpty().validate(male);
        assertThat(result, is(ValidationResult.pass()));
    }


    @Test
    void passWhenOntologyClassLabelIsWellFormatted() {
        ValidationResult result = OntologyClassValidators.checkLabelIsWellFormatted().validate(TestExamples.MALE);
        assertThat(result, is(ValidationResult.pass()));
    }

    //    ------------------------ FAILURES ---------------------------------------------

    @Test
    void failOnEmpty() {
        OntologyClass empty = OntologyClass.newBuilder().setId("").setLabel("").build();
        ValidationResult result = OntologyClassValidators.checkNotEmpty().validate(empty);
        assertThat(result, is(ValidationResult.fail("Ontology class must not be empty")));

        empty = OntologyClass.newBuilder().build();
        result = OntologyClassValidators.checkNotEmpty().validate(empty);
        assertThat(result, is(ValidationResult.fail("Ontology class must not be empty")));
    }


    @Test
    void failOnBadPrefixIdFormat() {
        // Pattern  "[A-Za-z_]+" - here zero in the prefix ID
        OntologyClass bad = OntologyClass.newBuilder().setId("PAT0:0000384").setLabel("male").build();
        ValidationResult result = OntologyClassValidators.checkIdIsWellFormatted().validate(bad);
        assertThat(result, is(ValidationResult.fail("Id prefix does not match '[A-Za-z_]+' pattern")));
    }


    @Test
    void failOnBadSuffixIdFormat() {
        // Pattern "\d+" -
        OntologyClass bad = OntologyClass.newBuilder().setId("PATO:O000384").setLabel("male").build();
        ValidationResult result = OntologyClassValidators.checkIdIsWellFormatted().validate(bad);
        assertThat(result, is(ValidationResult.fail("Id suffix does not match '\\d+' pattern")));
    }


    @Test
    void failOnEmptyId() {
        OntologyClass bad = OntologyClass.newBuilder().setId("").setLabel("male").build();
        ValidationResult result = OntologyClassValidators.checkIdIsWellFormatted().validate(bad);
        assertThat(result, is(ValidationResult.fail("Id string must not be empty")));
    }


    @Test
    void failOnMissingColonInIdString() {
        OntologyClass bad = OntologyClass.newBuilder().setId("PATO0000384").setLabel("male").build();
        ValidationResult result = OntologyClassValidators.checkIdIsWellFormatted().validate(bad);
        assertThat(result, is(ValidationResult.fail("Missing required ':' in the id string")));
    }


    @Test
    void failOnMissingLabel() {
        OntologyClass bad = OntologyClass.newBuilder().setLabel("").build();
        ValidationResult result = OntologyClassValidators.checkLabelIsWellFormatted().validate(bad);
        assertThat(result, is(ValidationResult.fail("Label string must not be empty")));
    }

}