package org.phenopackets.validator.ontology;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.OntologyClass;
import org.phenopackets.schema.v2.core.PhenotypicFeature;
import org.phenopackets.validator.core.ValidationItem;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HpoValidatorTest {

    public Ontology ontology;

    public HpoValidator validator;

    private static byte[] wrapInPhenopacketAndGetJsonBytes(List<PhenotypicFeature> phenotypicFeatures) throws InvalidProtocolBufferException {
        Phenopacket phenopacket = Phenopacket.newBuilder()
                .addAllPhenotypicFeatures(phenotypicFeatures)
                .build();
        return JsonFormat.printer().print(phenopacket).getBytes(StandardCharsets.UTF_8);
    }

    private static PhenotypicFeature makePhenotypicFeature(String termId, boolean excluded) {
        return PhenotypicFeature.newBuilder()
                .setType(OntologyClass.newBuilder().setId(termId).build())
                .setExcluded(excluded)
                .build();
    }

    // set up the Ontology mocks. There is no better way to test this unless we stash ~2mb hp.json.gz in the repo
    private static Ontology prepareMiniOntology() {
        Ontology ontology = mock(Ontology.class);
        TermId one = TermId.of("HP:0000001"); // the root
        TermId two = TermId.of("HP:0000002"); // one's child
        TermId three = TermId.of("HP:0000003"); // one's child
        TermId four = TermId.of("HP:0000004"); // three's child
        TermId five = TermId.of("HP:0000005"); // three's child
        when(ontology.containsTerm(one)).thenReturn(true);
        when(ontology.getPrimaryTermId(one)).thenReturn(one);
        when(ontology.containsTerm(two)).thenReturn(true);
        when(ontology.getPrimaryTermId(two)).thenReturn(two);
        when(ontology.containsTerm(three)).thenReturn(true);
        when(ontology.getPrimaryTermId(three)).thenReturn(three);
        when(ontology.containsTerm(four)).thenReturn(true);
        when(ontology.getPrimaryTermId(four)).thenReturn(four);
        when(ontology.containsTerm(five)).thenReturn(true);
        when(ontology.getPrimaryTermId(five)).thenReturn(five);

        when(ontology.getAncestorTermIds(one)).thenReturn(Set.of(one));
        when(ontology.getAncestorTermIds(two)).thenReturn(Set.of(two, one));
        when(ontology.getAncestorTermIds(three)).thenReturn(Set.of(three, one));
        when(ontology.getAncestorTermIds(four)).thenReturn(Set.of(four, three, one));
        when(ontology.getAncestorTermIds(five)).thenReturn(Set.of(five, three, one));

        return ontology;
    }

    @BeforeEach
    public void setUp() {
        ontology = prepareMiniOntology();
        validator = new HpoValidator(ontology);
    }

    @Nested
    public class NoErrorsInValidation {

        @Test
        public void disjointTerms() throws Exception {
            List<PhenotypicFeature> phenotypicFeatures = List.of(
                    makePhenotypicFeature("HP:0000002", false),
                    makePhenotypicFeature("HP:0000005", false));
            byte[] bytes = wrapInPhenopacketAndGetJsonBytes(phenotypicFeatures);

            List<ValidationItem> items = validator.validate(new ByteArrayInputStream(bytes));

            assertThat(items, hasSize(0));
        }

        @Test
        public void parentIsPresentWhileChildrenAreExcluded() throws Exception {
            List<PhenotypicFeature> phenotypicFeatures = List.of(
                    makePhenotypicFeature("HP:0000003", false),
                    makePhenotypicFeature("HP:0000004", true),
                    makePhenotypicFeature("HP:0000005", true)
            );
            byte[] bytes = wrapInPhenopacketAndGetJsonBytes(phenotypicFeatures);

            List<ValidationItem> items = validator.validate(new ByteArrayInputStream(bytes));

            assertThat(items, hasSize(0));
        }

    }

    @Nested
    public class ValidationFails {

        @Test
        public void termIdIsAbsentFromOntology() throws Exception {
            List<PhenotypicFeature> phenotypicFeatures = List.of(makePhenotypicFeature("HP:9999999", false));
            byte[] bytes = wrapInPhenopacketAndGetJsonBytes(phenotypicFeatures);

            List<ValidationItem> items = validator.validate(new ByteArrayInputStream(bytes));

            assertThat(items, hasSize(1));
            ValidationItem expected = ValidationItem.of(validator.validatorInfo, OntologyValidationItemTypes.ONTOLOGY_INVALID_ID, "Could not find term id: HP:9999999");
            assertThat(items.get(0), equalTo(expected));
        }

        @Test
        public void nonPrimaryTermIdIsUsed() throws Exception {
            List<PhenotypicFeature> phenotypicFeatures = List.of(makePhenotypicFeature("HP:9999999", false));
            byte[] bytes = wrapInPhenopacketAndGetJsonBytes(phenotypicFeatures);

            when(ontology.containsTerm(TermId.of("HP:9999999"))).thenReturn(true);
            when(ontology.getPrimaryTermId(TermId.of("HP:9999999"))).thenReturn(TermId.of("HP:0000001"));

            List<ValidationItem> items = validator.validate(new ByteArrayInputStream(bytes));

            assertThat(items, hasSize(1));
            ValidationItem expected = ValidationItem.of(validator.validatorInfo,
                    OntologyValidationItemTypes.ONTOLOGY_TERM_WITH_ALTERNATE_ID,
                    "Using alternate (obsolete) id (HP:9999999) instead of primary id (HP:0000001)");
            assertThat(items.get(0), equalTo(expected));
        }

        @Test
        public void usingTermAlongWithItsAncestor() throws Exception {
            List<PhenotypicFeature> phenotypicFeatures = List.of(
                    makePhenotypicFeature("HP:0000003", false),
                    makePhenotypicFeature("HP:0000004", false));
            byte[] bytes = wrapInPhenopacketAndGetJsonBytes(phenotypicFeatures);


            List<ValidationItem> items = validator.validate(new ByteArrayInputStream(bytes));

            assertThat(items, hasSize(1));
            ValidationItem expected = ValidationItem.of(validator.validatorInfo,
                    OntologyValidationItemTypes.TERM_AND_ANCESTOR_ARE_USED,
                    "Using both term HP:0000004 and its ancestor HP:0000003");
            assertThat(items.get(0), equalTo(expected));
        }

        @Test
        public void usingTermWhileParentTermIsExcluded() throws Exception {
            List<PhenotypicFeature> phenotypicFeatures = List.of(
                    makePhenotypicFeature("HP:0000003", true),
                    makePhenotypicFeature("HP:0000004", false));
            byte[] bytes = wrapInPhenopacketAndGetJsonBytes(phenotypicFeatures);


            List<ValidationItem> items = validator.validate(new ByteArrayInputStream(bytes));

            assertThat(items, hasSize(1));
            ValidationItem expected = ValidationItem.of(validator.validatorInfo,
                    OntologyValidationItemTypes.TERM_IS_USED_AND_ANCESTOR_IS_EXCLUDED,
                    "Term HP:0000004 is present while its ancestor HP:0000003 is excluded");
            assertThat(items.get(0), equalTo(expected));
        }
    }

}