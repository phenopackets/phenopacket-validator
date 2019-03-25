package org.phenopackets.schema.validator.core.util;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Phenotype;
import org.phenopackets.schema.validator.core.TestExamples;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.phenopackets.schema.validator.core.TestExamples.*;

class MessageUtilsTest {

    @Test
    void instancesInNonrepeatedFieldsAreIdentified() {
        Phenopacket pp = TestExamples.getJohnnyPhenopacket();

        List<Individual> individuals = MessageUtils.getEmbeddedMessageFieldsOfType(pp, Individual.class);
        assertThat(individuals.size(), is(1));
        assertThat(individuals, hasItem(TestExamples.getIndividualJohnny()));
    }

    /**
     * Test that all {@link Phenotype}s are fetched (repeated field).
     */
    @Test
    void instancesInRepeatedFieldsAreIdentified() {
        Phenopacket pp = Phenopacket.newBuilder()
                .addPhenotypes(TestExamples.spherocytosisWithChildhoodOnset())
                .addPhenotypes(TestExamples.hepatosplenomegalyWithAdultOnset())
                .build();

        List<Phenotype> phenotypes = MessageUtils.getEmbeddedMessageFieldsOfType(pp, Phenotype.class);

        assertThat(phenotypes.size(), is(2));
        assertThat(phenotypes, hasItems(TestExamples.spherocytosisWithChildhoodOnset(), TestExamples.hepatosplenomegalyWithAdultOnset()));
    }


    /**
     * Here we test that {@link OntologyClass} instances are retrieved from {@link Phenopacket} even though they are
     * attributes of {@link Phenotype} and not {@link Phenopacket} directly.
     */
    @Test
    void nestedInstancesAreIdentified() {
        Phenopacket pp = Phenopacket.newBuilder()
                .addPhenotypes(TestExamples.spherocytosisWithChildhoodOnset())
                .addPhenotypes(TestExamples.hepatosplenomegalyWithAdultOnset())
                .build();

        List<OntologyClass> ocs = MessageUtils.getEmbeddedMessageFieldsOfType(pp, OntologyClass.class);

        assertThat(ocs.size(), is(4));
        assertThat(ocs, hasItems(ontologyClass("HP:0004444", "Spherocytosis"), CHILDHOOD_ONSET,
                ontologyClass("HP:0001433", "Hepatosplenomegaly"), ADULT_ONSET));
    }
}