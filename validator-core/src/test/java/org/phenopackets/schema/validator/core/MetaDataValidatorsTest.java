package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Resource;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 *
 * @author Daniel Danis <daniel.danis@jax.org>
 */
class MetaDataValidatorsTest {


    // ------------------------ checkMetaDataNotEmpty -------------------------------------
    @Test
    void passWhenMetaDataIsNotEmpty() {
        MetaData md = TestExamples.makeValidMetadata();
        ValidationResult result = MetaDataValidators.checkMetaDataNotEmpty().validate(md);

        assertThat(result, is(ValidationResult.pass()));
    }

    /**
     * Each valid HPO Phenopacket needs to have a MetaData section. Here we construct a Phenopacket
     * that does not have a MetaData section and show that it is invalid.
     */
    @Test
    void checkFailureOnEmptyMetaData() {
        MetaData md = MetaData.getDefaultInstance();
        ValidationResult result = MetaDataValidators.checkMetaDataNotEmpty().validate(md);

        assertThat(result, is(ValidationResult.fail("Metadata is empty")));
    }

    // ------------------------ checkCreatedIsNotEmpty --------------------------------------
    @Test
    void passWhenCreatedIsPresent() {
        MetaData md = TestExamples.makeValidMetadata();
        ValidationResult result = MetaDataValidators.checkCreatedIsNotEmpty().validate(md);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void checkCreatedByIsMissing() {
        Phenopacket pp = Phenopacket.newBuilder().build();
        ValidationResult result = MetaDataValidators.checkCreatedIsNotEmpty().validate(pp.getMetaData());

        assertThat(result, is(ValidationResult.fail("Missing timestamp for when this phenopacket was created (field 'created')")));
    }

    // ------------------------ checkCreatedByIsNotEmpty --------------------------------------
    @Test
    void passWhenCreatedByIsPresent() {
        MetaData md = TestExamples.makeValidMetadata();
        ValidationResult result = MetaDataValidators.checkCreatedByIsNotEmpty().validate(md);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenCreatedByIsMissing() {
        MetaData md = MetaData.getDefaultInstance();
        ValidationResult result = MetaDataValidators.checkCreatedByIsNotEmpty().validate(md);

        assertThat(result, is(ValidationResult.fail("Missing author information (field 'created_by')")));
    }

    // ------------------------ checkSubmittedByIsNotEmpty --------------------------------------
    @Test
    void passWhenSubmittedByIsPresent() {
        MetaData md = TestExamples.makeValidMetadata();
        ValidationResult result = MetaDataValidators.checkSubmittedByIsNotEmpty().validate(md);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenSubmittedByIsEmpty() {
        MetaData md = MetaData.getDefaultInstance();
        ValidationResult result = MetaDataValidators.checkSubmittedByIsNotEmpty().validate(md);

        assertThat(result, is(ValidationResult.fail("Missing information about the person/organisation/network that has " +
                "submitted the phenopacket (field 'submitted_by')")));
    }

    // ------------------------ checkMetaDataHasNoEmptyResource --------------------------------------
    @Test
    void passOnMetadataWithNonEmptyResources() {
        MetaData md = TestExamples.makeValidMetadata();
        List<ValidationResult> results = MetaDataValidators.checkMetaDataHasNoEmptyResource().validate(md);

        assertThat(results.isEmpty(), is(true));
    }


    /**
     * {@link org.phenopackets.schema.v1.core.MetaData} is not allowed to contain empty {@link org.phenopackets.schema.v1.core.Resource}.
     */
    @Test
    void failOnEmptyResourceInMetaData() {
        Phenopacket pp = Phenopacket.newBuilder()
                // add empty Resource to induce failure
                .setMetaData(TestExamples.makeValidMetadata().toBuilder().addResources(Resource.newBuilder().build()))
                .build();
        List<ValidationResult> results = MetaDataValidators.checkMetaDataHasNoEmptyResource().validate(pp.getMetaData());

        assertThat(results.size(), equalTo(1));
        assertThat(results, hasItem(ValidationResult.fail("Resource is empty")));
    }

}