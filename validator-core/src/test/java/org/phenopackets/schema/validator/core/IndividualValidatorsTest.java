package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Biosample;
import org.phenopackets.schema.v1.core.Individual;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.phenopackets.schema.validator.core.TestExamples.ontologyClass;

class IndividualValidatorsTest {

    // ------------------------ checkThatSubjectIsNotEmpty -------------------------------------

    @Test
    void passWhenSubjectIsNotEmpty() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();
        ValidationResult result = IndividualValidators.checkThatSubjectIsNotEmpty().validate(pp);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenSubjectIsMissing() {
        Phenopacket pp = Phenopacket.getDefaultInstance();
        ValidationResult result = IndividualValidators.checkThatSubjectIsNotEmpty().validate(pp);

        assertThat(result, is(ValidationResult.fail("Subject must not be empty")));
    }

    // ------------------------ checkNotEmpty -------------------------------------


    @Test
    void passWhenIndividualIsNotEmpty() {
        Individual johnny = TestExamples.getIndividualJohnny();
        ValidationResult result = IndividualValidators.checkNotEmpty().validate(johnny);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenIndividualIsEmpty() {
        Individual i = Individual.getDefaultInstance();
        ValidationResult result = IndividualValidators.checkNotEmpty().validate(i);

        assertThat(result, is(ValidationResult.fail("Individual must not be empty")));
    }

    // ------------------------ checkId -------------------------------------

    @Test
    void passWhenIndividualHasId() {
        Individual johnny = TestExamples.getIndividualJohnny();
        ValidationResult result = IndividualValidators.checkId().validate(johnny);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenIndividualDoesNotHaveId() {
        Individual i = Individual.getDefaultInstance();
        ValidationResult result = IndividualValidators.checkId().validate(i);

        assertThat(result, is(ValidationResult.fail("Individual must have an id")));
    }

    // ------------------------ checkThatSubjectIdCorrespondsToBiosampleIndividualIds -------------------------------------


    @Test
    void passWhenBiosampleIndividualIdsMatchSubjectId() {

        Phenopacket pp = TestExamples.getJohnnyPhenopacket();
        String subjectId = pp.getSubject().getId();
        pp = pp.toBuilder()
                .addBiosamples(Biosample.newBuilder()
                        .setIndividualId(subjectId)
                        .setSampledTissue(ontologyClass("UBERON:0003949", "tubal tonsil"))
                        .build())
                .addBiosamples(Biosample.newBuilder()
                        .setIndividualId(subjectId)
                        .setSampledTissue(ontologyClass("UBERON:0001732", "pharyngeal tonsil"))
                        .build())
                .build();

        List<ValidationResult> results = IndividualValidators.checkThatSubjectIdCorrespondsToBiosampleIndividualIds().validate(pp);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void failWhenBiosampleIndividualIdDoesNotMatchSubjectId() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();
        pp = pp.toBuilder()
                .addBiosamples(Biosample.newBuilder()
                        .setId("tubal_tonsil")
                        .setDatasetId("test")
                        .setIndividualId("other")
                        .setSampledTissue(ontologyClass("UBERON:0003949", "tubal tonsil"))
                        .build())
                .addBiosamples(Biosample.newBuilder()
                        .setId("pharyngeal_tonsil")
                        .setDatasetId("test")
                        .setIndividualId("yet another")
                        .setSampledTissue(ontologyClass("UBERON:0001732", "pharyngeal tonsil"))
                        .build())
                .build();

        List<ValidationResult> results = IndividualValidators.checkThatSubjectIdCorrespondsToBiosampleIndividualIds().validate(pp);

        assertThat(results.size(), is(2));
        assertThat(results, hasItems(
                ValidationResult.fail("Individual id 'other' found in biosample 'tubal_tonsil:test' does not match subject's id 'Johnny'"),
                ValidationResult.fail("Individual id 'yet another' found in biosample 'pharyngeal_tonsil:test' does not match subject's id 'Johnny'")));
    }

    @Test
    void passWhenThereAreNoBiosamples() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();

        List<ValidationResult> results = IndividualValidators.checkThatSubjectIdCorrespondsToBiosampleIndividualIds().validate(pp);

        assertThat(results.isEmpty(), is(true));
    }
}