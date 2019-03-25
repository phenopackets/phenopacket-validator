package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Resource;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class MetaDataValidatorsTest {


    /**
     * Each valid HPO Phenopacket needs to have a MetaData section. Here we construct a Phenopacket
     * that does not have a MetaData section and show that it is invalid.
     */
    @Test
    void checkFailureOnEmptyMetaData() {
        Phenopacket pp = Phenopacket.newBuilder().build();
        List<ValidationResult> results = MetaDataValidators.checkMetaDataNotEmpty().validate(pp.getMetaData());

        assertThat(results.size(), equalTo(1));
        assertThat(results, hasItem(ValidationResult.fail("Metadata is empty")));
    }

    @Test
    void checkCreatedByIsMissing() {
        Phenopacket pp = Phenopacket.newBuilder().build();
        List<ValidationResult> results = MetaDataValidators.checkCreatedByIsNotEmpty().validate(pp.getMetaData());

        assertThat(results.size(), equalTo(1));
        assertThat(results, hasItem(ValidationResult.fail("Missing author information (field created_by)")));
    }

    /**
     * {@link org.phenopackets.schema.v1.core.MetaData} is not allowed to contain empty {@link org.phenopackets.schema.v1.core.Resource}.
     */
    @Test
    void checkFailureOnEmptyResourceInMetaData() {
        Phenopacket pp = Phenopacket.newBuilder()
                // add empty metadata
                .setMetaData(TestExamples.makeValidMetadata().toBuilder().addResources(Resource.newBuilder().build()))
                .build();
        List<ValidationResult> results = MetaDataValidators.checkMetaDataHasNoEmptyResource().validate(pp.getMetaData());

        assertThat(results.size(), equalTo(1));
        assertThat(results, hasItem(ValidationResult.fail("Resource is empty")));
    }

    @Test
    void checkPassOnMetadataWithNonEmptyResources() {
        Phenopacket pp = Phenopacket.newBuilder().setMetaData(TestExamples.makeValidMetadata()).build();

        List<ValidationResult> results = MetaDataValidators.checkMetaDataHasNoEmptyResource().validate(pp.getMetaData());

        assertThat(results, not(hasItem(ValidationResult.fail("Resource is empty"))));
    }
}