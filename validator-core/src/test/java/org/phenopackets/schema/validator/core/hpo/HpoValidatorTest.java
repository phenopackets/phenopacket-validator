package org.phenopackets.schema.validator.core.hpo;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v1.PhenoPacket;

import org.phenopackets.schema.v1.core.*;
import org.phenopackets.schema.validator.core.ValidationResult;



import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.util.List;


/**
 * This class includes some convenience functions for making valid and invalid Phenopackets that we will use to
 * test the validation machinery.
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 * @author Peter Robinson <peter.robinson@jax.org>
 */
class HpoValidatorTest {
    @Mock
    HpoOntology ontology;

    /** convenience function for creating an OntologyClass object. */
    private static OntologyClass ontologyClass(String id, String label) {
        return OntologyClass.newBuilder()
                .setId(id)
                .setLabel(label)
                .build();
    }


    /** Example phenopacket -- we still need to develop this for testing.*/

    public PhenoPacket spherocytosisExample() {
        final String PROBAND_ID = "PROBAND#1";
        final OntologyClass FEMALE = ontologyClass("PATO:0000383", "female");
        Phenotype spherocytosis = Phenotype.newBuilder()
                .setType(ontologyClass("HP:0004444", "Spherocytosis"))
                .setClassOfOnset(ontologyClass("HP:0011463", "Childhood onset"))
                .build();
        Phenotype jaundice = Phenotype.newBuilder()
                .setType(ontologyClass("HP:0000952", "Jaundice"))
                .setClassOfOnset(ontologyClass("HP:0011463", "Childhood onset"))
                .build();
        Phenotype splenomegaly = Phenotype.newBuilder()
                .setType(ontologyClass("HP:0001744", "Splenomegaly"))
                .setClassOfOnset(ontologyClass("HP:0011463", "Childhood onset"))
                .build();
        Phenotype notHepatomegaly = Phenotype.newBuilder()
                .setType(ontologyClass("HP:0002240", "Hepatomegaly"))
                .setNegated(true)
                .build();
        Phenotype reticulocytosis = Phenotype.newBuilder()
                .setType(ontologyClass("HP:0001923", "Reticulocytosis"))
                .build();
        Variant ANK1_variant = Variant.newBuilder()
                .setSequence("NM_001142446.1")
                .setPosition(5620)
                .setDeletion("C")
                .setInsertion("T")
                .setHgvs("NM_001142446.1:c.5620C>T ")
                .putSampleGenotypes(PROBAND_ID, ontologyClass("GENO:0000135", "heterozygous"))
                .build();

        Individual proband = Individual.newBuilder()
                .setSex(FEMALE)
                .setId(PROBAND_ID)
                .setAgeAtCollection(Age.newBuilder().setAge("P27Y3M").build())
                .addPhenotypes(spherocytosis)
                .addPhenotypes(jaundice)
                .addPhenotypes(splenomegaly)
                .addPhenotypes(notHepatomegaly)
                .addPhenotypes(reticulocytosis)
                .build();

        MetaData metaData = createValidMetadata();

        return PhenoPacket.newBuilder()
                .setSubject(proband)
                .addAllVariants(ImmutableList.of(ANK1_variant))
                .setMetaData(metaData)
                .build();
    }




    private  MetaData createValidMetadata() {
        return  MetaData.newBuilder()
                .addResources(Resource.newBuilder()
                        .setId("hp")
                        .setName("human phenotype ontology")
                        .setNamespacePrefix("HP")
                        .setIriPrefix("http://purl.obolibrary.org/obo/HP_")
                        .setUrl("http://purl.obolibrary.org/obo/hp.owl")
                        .setVersion("2018-03-08")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("pato")
                        .setName("Phenotype And Trait Ontology")
                        .setNamespacePrefix("PATO")
                        .setIriPrefix("http://purl.obolibrary.org/obo/PATO_")
                        .setUrl("http://purl.obolibrary.org/obo/pato.owl")
                        .setVersion("2018-03-28")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("geno")
                        .setName("Genotype Ontology")
                        .setNamespacePrefix("GENO")
                        .setIriPrefix("http://purl.obolibrary.org/obo/GENO_")
                        .setUrl("http://purl.obolibrary.org/obo/geno.owl")
                        .setVersion("19-03-2018")
                        .build())
                .setCreatedBy("Example clinician")
                .build();
    }


    Phenotype createValidSpherocytosisTerm() {
        return Phenotype.newBuilder()
                .setType(ontologyClass("HP:0004444", "Spherocytosis"))
                .setClassOfOnset(ontologyClass("HP:0011463", "Childhood onset"))
                .build();
    }




    /** Every valid HPO Phenopacket needs to have a MetaData section. Here we construct a Phenopacket
     * that does not have a MetaData section and show that it is invalid.
     */
    @Test
    void testWhetherPhenopacketContainsMetadata() {
        Individual subject = Individual.newBuilder().build();
        PhenoPacket phenoPacket = PhenoPacket.newBuilder()
                .setSubject(subject)
                .build();

        HpoValidator validator = new HpoValidator(ontology);
        validator.validate(phenoPacket);
        List<ValidationResult> result = validator.validate(phenoPacket);
        System.out.println(result);
//        assertThat(result.isValid(), equalTo(false));
    }


    /** Every valid HPO Phenopacket needs to have a subject. Here we construct a Phenopacket
     * that does not have a subject and show that it is invalid.
     */
    @Test
    void testWhetherPhenopacketHasSubject() {
        MetaData metaData = createValidMetadata();
        PhenoPacket phenoPacket = PhenoPacket.newBuilder()
                .setMetaData(metaData)
                .build();

        HpoValidator validator = new HpoValidator(ontology);
        validator.validate(phenoPacket);
        List<ValidationResult> result = validator.validate(phenoPacket);
        System.out.println(result);
        //assertThat(result.isValid(), equalTo(false));
    }



//    @Test
//    void testObsoleteHpoTerm() {
//        when(ontology.getObsoleteTermIds().contains(anyObject())).thenReturn(true);
//
//        Phenotype spherocytosis = createValidSpherocytosisTerm();
//        Individual subject = Individual.newBuilder().addPhenotypes(spherocytosis).build();
//        MetaData metaData = createValidMetadata();
//
//        PhenoPacket phenoPacket = PhenoPacket.newBuilder()
//                .setSubject(subject)
//                .setMetaData(metaData)
//                .build();
//        HpoValidator validator = new HpoValidator(ontology);
//        validator.validate(phenoPacket);
//        ValidationResult result = validator.validate(phenoPacket);
//        System.out.println(result);
//        assertThat(result.isValid(), equalTo(false));
//    }








}