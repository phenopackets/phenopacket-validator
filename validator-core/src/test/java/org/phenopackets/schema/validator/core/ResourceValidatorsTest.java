package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Age;
import org.phenopackets.schema.v1.core.Disease;
import org.phenopackets.schema.v1.core.Resource;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.phenopackets.schema.validator.core.TestExamples.ontologyClass;


class ResourceValidatorsTest {

    // ------------------------ SUCCESSES--------------------------------------

    @Test
    void passWhenResourceIsValid() {
        ValidationResult result = ResourceValidators.checkResourceIsNotEmpty().validate(TestExamples.getGenoResource());

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void passWhenUrlIsValid() {
        ValidationResult result = ResourceValidators.checkUrlSyntax().validate(TestExamples.getGenoResource());

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void passWhenAllResourcesAreUsed() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();
        List<ValidationResult> results = ResourceValidators.checkUnusedResources().validate(pp);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void passWhenNoUndefinedResource() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();
        List<ValidationResult> results = ResourceValidators.checkUndefinedResorces().validate(pp);

        assertThat(results.isEmpty(), is(true));
    }

    // ------------------------ FAILURES --------------------------------------

    @Test
    void failOnEmptyResource() {
        ValidationResult result = ResourceValidators.checkResourceIsNotEmpty().validate(Resource.newBuilder().build());

        assertThat(result, is(ValidationResult.fail("Resource is empty")));
    }


    @Test
    void failOnMalformedUrlMissingProtocol() {
        Resource geno = TestExamples.getGenoResource().toBuilder().setUrl("http").build();

        ValidationResult result = ResourceValidators.checkUrlSyntax().validate(geno);
        assertThat(result, is(ValidationResult.fail("Malformed URL - no protocol: http")));
    }

    @Test
    void failOnUnusedResource() {
        Phenopacket.Builder ppBuilder = TestExamples.getJohnnyPhenopacket().toBuilder();
        ppBuilder.getMetaDataBuilder().addResources(TestExamples.getPatoResource());
        Phenopacket pp = ppBuilder.build();

        List<ValidationResult> results = ResourceValidators.checkUnusedResources().validate(pp);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Unused resource 'Phenotype And Trait Ontology'")));
    }

    @Test
    void failOnPhenopacketWithUndefinedResource() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket().toBuilder()
                .addDiseases(Disease.newBuilder()
                        .setTerm(ontologyClass("OMIM:270970", "Spherocytosis, Autosomal Recessive"))
                        .setAgeOfOnset(Age.newBuilder().setAge("P5Y2M").build())
                        .build())
                .build();

        List<ValidationResult> results = ResourceValidators.checkUndefinedResorces().validate(pp);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Undefined resource for namespace 'OMIM' used in term " +
                "'OMIM:270970 - Spherocytosis, Autosomal Recessive'")));
    }
}