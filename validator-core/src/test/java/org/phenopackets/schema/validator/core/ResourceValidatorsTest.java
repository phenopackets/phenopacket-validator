package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Age;
import org.phenopackets.schema.v1.core.Disease;
import org.phenopackets.schema.v1.core.Resource;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.phenopackets.schema.validator.core.ResourceValidators.IRI_PREFIX;
import static org.phenopackets.schema.validator.core.examples.TestExamples.ontologyClass;


/**
 *
 * @author Daniel Danis <daniel.danis@jax.org>
 */
class ResourceValidatorsTest {

    // ------------------------ checkResourceIsNotEmpty -------------------------------------

    @Test
    void passWhenResourceIsValid() {
        ValidationResult result = ResourceValidators.checkResourceIsNotEmpty().validate(TestExamples.getGenoResource());

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failOnEmptyResource() {
        ValidationResult result = ResourceValidators.checkResourceIsNotEmpty().validate(Resource.newBuilder().build());

        assertThat(result, is(ValidationResult.fail("Resource is empty")));
    }

    // ------------------------ checkUrlSyntax --------------------------------------

    @Test
    void passWhenUrlIsValid() {
        Resource rs = TestExamples.getGenoResource();
        ValidationResult result = ResourceValidators.checkUrlSyntax().validate(rs);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failOnMalformedUrlMissingProtocol() {
        Resource rs = TestExamples.getGenoResource().toBuilder().setUrl("http").build();

        ValidationResult result = ResourceValidators.checkUrlSyntax().validate(rs);
        assertThat(result, is(ValidationResult.fail("Malformed URL - no protocol: http")));
    }

    // ------------------------ checkUnusedResources --------------------------------------

    @Test
    void passWhenAllResourcesAreUsed() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();
        List<ValidationResult> results = ResourceValidators.checkUnusedResources().validate(pp);

        assertThat(results.isEmpty(), is(true));
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


    // ------------------------ checkUndefinedResorces --------------------------------------

    @Test
    void passWhenNoUndefinedResource() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();
        List<ValidationResult> results = ResourceValidators.checkUndefinedResorces().validate(pp);

        assertThat(results.isEmpty(), is(true));
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

    @Test
    void ignorePhenopacketContainingMisformattedOntologyClassInstance() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket().toBuilder()
                .addVariants(TestExamples.getSURF1Variant(ontologyClass("", "1234567"))) // missing idspace in genotype
                .build();

        List<ValidationResult> results = ResourceValidators.checkUndefinedResorces().validate(pp);

        assertThat(results.isEmpty(), is(true));
    }

    // ------------------------ checkNamespacePrefixForOboResource --------------------------------------

    @Test
    void passWhenIriPrefixMatchesNamespacePrefix() {
        Resource hpoRs = TestExamples.getHpoResource();

        ValidationResult result = ResourceValidators.checkNamespacePrefixForOboResource().validate(hpoRs);
        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenIriPrefixDoesNotMatchNamespacePrefix() {
        Resource rs = Resource.newBuilder()
                .setNamespacePrefix("HP")
                .setIriPrefix("http://purl.obolibrary.org/obo/GENO_")
                .build();

        ValidationResult result = ResourceValidators.checkNamespacePrefixForOboResource().validate(rs);
        assertThat(result, is(ValidationResult.fail("Namespace prefix 'HP' does not match 'GENO' present in IRI prefix 'http://purl.obolibrary.org/obo/GENO_'")));
    }

    @Test
    void failWhenIriPrefixIsMisformatted() {
        Resource rs = Resource.newBuilder().setIriPrefix("http://purl.obolibrary.org/obo/G0_").build();

        ValidationResult result = ResourceValidators.checkNamespacePrefixForOboResource().validate(rs);
        assertThat(result, is(ValidationResult.fail(String.format("IRI prefix 'http://purl.obolibrary.org/obo/G0_' does not match " +
                "pattern '%s'", IRI_PREFIX.pattern()))));
    }

    // ------------------------ checkIriPrefixIsWellFormattedForOboResource --------------------------------------


    @Test
    void passWhenIriPrefixIsWellFormatedForOboResource() {
        Resource rs = TestExamples.getGenoResource();

        ValidationResult result = ResourceValidators.checkIriPrefixIsWellFormattedForOboResource().validate(rs);
        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenIriPrefixIsNotWellFormatedForOboResource() {
        Resource rs = Resource.newBuilder().setIriPrefix("http://purl.obolibrary.org/obo/G0_").build();

        ValidationResult result = ResourceValidators.checkIriPrefixIsWellFormattedForOboResource().validate(rs);
        assertThat(result, is(ValidationResult.fail(String.format("IRI prefix 'http://purl.obolibrary.org/obo/G0_' does " +
                "not match pattern '%s'", IRI_PREFIX.pattern()))));
    }

    // ------------------------ checkRequiredDataIsNotEmpty ------------------------------------------------------


    @Test
    void faliWhenRequiredDataIsEmpty() {
        Resource r = Resource.getDefaultInstance();
        List<ValidationResult> results = ResourceValidators.checkRequiredDataIsNotEmpty().validate(r);

        assertThat(results.size(), is(6));
        assertThat(results, hasItems(
                ValidationResult.fail("Resource id must not be empty"),
                ValidationResult.fail("Resource name must not be empty"),
                ValidationResult.fail("Resource namespace prefix must not be empty"),
                ValidationResult.fail("Resource url must not be empty"),
                ValidationResult.fail("Resource version must not be empty"),
                ValidationResult.fail("Resource iri prefix must not be empty")
                ));
    }

    @Test
    void passWhenRequiredDataIsNotEmpty() {
        Resource r = TestExamples.getHpoResource();
        List<ValidationResult> results = ResourceValidators.checkRequiredDataIsNotEmpty().validate(r);

        assertThat(results.size(), is(0));
    }
}