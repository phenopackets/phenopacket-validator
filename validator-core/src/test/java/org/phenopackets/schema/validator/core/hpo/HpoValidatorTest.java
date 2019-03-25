package org.phenopackets.schema.validator.core.hpo;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.Phenotype;
import org.phenopackets.schema.validator.core.TestExamples;
import org.phenopackets.schema.validator.core.ValidationResult;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;


/**
 * This class includes some convenience functions for making valid and invalid Phenopackets that we will use to
 * test the validation machinery.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 * @author Peter Robinson <peter.robinson@jax.org>
 */
@ExtendWith(MockitoExtension.class)
class HpoValidatorTest {

    @Mock
    private static Ontology ontology;

    private HpoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new HpoValidator(ontology);
    }

    @Test
    void nonObsoleteHpoTerm() {
        when(ontology.getObsoleteTermIds()).thenReturn(Collections.emptySet());

        Phenotype spherocytosis = TestExamples.spherocytosisWithChildhoodOnset();
        PhenoPacket phenoPacket = PhenoPacket.newBuilder().addPhenotypes(spherocytosis).build();

        List<ValidationResult> results = validator.validate(phenoPacket);

        assertThat(results, not(hasItem(ValidationResult.fail("Phenopacket has an obsolete term with id: HP:0004444"))));
    }


    @Test
    void obsoleteHpoTermIsUsed() {
        Set<TermId> obsoleteTermsMock = Sets.newHashSet(TermId.of("HP:0006876"), TermId.of("HP:0004444"));
        when(ontology.getObsoleteTermIds()).thenReturn(obsoleteTermsMock);

        Phenotype peripheralAxonalDegeneration = TestExamples.obsoletePeripheralAxonalDegenerationWithChildhoodOnset();
        PhenoPacket phenoPacket = PhenoPacket.newBuilder()
                .addPhenotypes(peripheralAxonalDegeneration)
                .build();

        final List<ValidationResult> results = validator.validate(phenoPacket);

        assertThat(results, hasItem(ValidationResult.fail("Phenopacket has an obsolete term with id: HP:0006876")));
    }

    /**
     *
     */
    @Test
    void phenopacketContainsTermAndItsAncestor() {
        TermId spherocytosis = TermId.of("HP:0004444"); // term
        TermId poikilocytosis = TermId.of("HP:0004447"); // ancestor
        TermId hepatosplenomegaly = TermId.of("HP:0001433"); // not related term

        Set<TermId> poikilocytosisAncestors = getAnemiaAndPoikilocytosisAncestors();
        Set<TermId> spherocytosisAncestors = new HashSet<>(poikilocytosisAncestors);
        spherocytosisAncestors.add(poikilocytosis);
        Set<TermId> hepatosplenomegalyAncestors = getHepatosplenomegalyAncestors();

        doReturn(poikilocytosisAncestors).when(ontology).getAncestorTermIds(poikilocytosis);
        doReturn(spherocytosisAncestors).when(ontology).getAncestorTermIds(spherocytosis);
        doReturn(hepatosplenomegalyAncestors).when(ontology).getAncestorTermIds(hepatosplenomegaly);

        PhenoPacket phenoPacket = PhenoPacket.newBuilder()
                .addPhenotypes(TestExamples.spherocytosisWithChildhoodOnset())
                .addPhenotypes(TestExamples.poikilocytosisWithChildhoodOnset())
                .addPhenotypes(TestExamples.hepatosplenomegalyWithAdultOnset())
                .build();

        List<ValidationResult> results = validator.validate(phenoPacket);

        assertThat(results, hasItem(ValidationResult.fail("PhenoPacket contains term as well as its ancestor - term: 'HP:0004444 - Spherocytosis', ancestor: 'HP:0004447 - Poikilocytosis'")));
    }

    @Test
    void phenopacketDoesNotContainAncestorOfTerms() {
        TermId anemia = TermId.of("HP:0001903");
        TermId poikilocytosis = TermId.of("HP:0004447");
        TermId hepatosplenomegaly = TermId.of("HP:0001433");

        Set<TermId> anemiaAndPoikilocytosisAncestors = getAnemiaAndPoikilocytosisAncestors();
        Set<TermId> hepatosplenomegalyAncestors = getHepatosplenomegalyAncestors();

        doReturn(anemiaAndPoikilocytosisAncestors).when(ontology).getAncestorTermIds(poikilocytosis);
        doReturn(anemiaAndPoikilocytosisAncestors).when(ontology).getAncestorTermIds(anemia);
        doReturn(hepatosplenomegalyAncestors).when(ontology).getAncestorTermIds(hepatosplenomegaly);

        PhenoPacket phenoPacket = PhenoPacket.newBuilder()
                .addPhenotypes(TestExamples.anemiaWithChildhoodOnset())
                .addPhenotypes(TestExamples.poikilocytosisWithChildhoodOnset())
                .addPhenotypes(TestExamples.hepatosplenomegalyWithAdultOnset())
                .build();

        List<ValidationResult> results = validator.validate(phenoPacket);

        assertTrue(results.isEmpty());
    }

    private Set<TermId> getAnemiaAndPoikilocytosisAncestors() {
        return Sets.newHashSet(TermId.of("HP:0001877"), // Abnormal erythrocyte morphology
                TermId.of("HP:0001871"), // Abnormality of blood and blood-forming tissues
                TermId.of("HP:0000118")); // Phenotypic abnormality
    }


    private Set<TermId> getHepatosplenomegalyAncestors() {
        return Sets.newHashSet(TermId.of("HP:0003271"), // Visceromegaly
                TermId.of("HP:0001438"), // Abnormality of abdomen morphology
                TermId.of("HP:0025031"), // Abnormality of the digestive system
                TermId.of("HP:0000118")); // Phenotypic abnormality
    }
}