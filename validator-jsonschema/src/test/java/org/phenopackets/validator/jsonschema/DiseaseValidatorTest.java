package org.phenopackets.validator.jsonschema;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Test;
import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.DiseaseBuilder;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.Disease;
import org.phenopackets.schema.v2.core.TimeElement;
import org.phenopackets.validator.core.ValidationItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.phenopackets.phenotools.builder.builders.OntologyClassBuilder.ontologyClass;
import static org.phenopackets.validator.jsonschema.JsonValidationItemTypes.*;
import static org.phenopackets.validator.testdatagen.DatagenBase.*;

/**
 * This class creates a simple phenopacket with a Disease object and creates some variations with
 * validation errors.
 * @author Peter N Robinson
 */
public class DiseaseValidatorTest extends JsonSchemaValidatorTestBase {

    private static Phenopacket phenopacketWithDisease() {
        var nyha3 = ontologyClass("NCIT:C66907", "New York Heart Association Class III");
        var childhood = TimeElement.newBuilder().setOntologyClass(CHILDHOOD_ONSET).build();
        var chagasCardiomyopathy = DiseaseBuilder.create("MONDO:0005491", "Chagas cardiomyopathy")
                .diseaseStage(nyha3)
                .onset(childhood)
                .build();
        return PhenopacketBuilder.create("id:A",metadataWithHpo)
                .disease(chagasCardiomyopathy)
                .build();
    }

    private static final Phenopacket phenopacket = phenopacketWithDisease();

    @Test
    public void testPhenopacketValidity() throws InvalidProtocolBufferException {
        String json =  JsonFormat.printer().print(phenopacket);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testLacksId() throws InvalidProtocolBufferException {
        // the Phenopacket is not valid if we remove the id
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).clearId().build();
        String json =  JsonFormat.printer().print(p1);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e: errors) {
            System.out.println(e);
        }
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(JSON_REQUIRED, error.type());
        assertEquals("$.id: is missing but it is required", error.message());
    }

    @Test
    public void testDiseaseLacksOntologyTerm() throws InvalidProtocolBufferException {
        // Disease must have an ontology term
        // the phenopacket has a single Disease message
        Disease disease = phenopacket.getDiseases(0);
        // remove the Ontology Term from the disease
        disease = Disease.newBuilder(disease).clearTerm().build();
        // replace the original disease object with the new disease object
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket)
                .setDiseases(0, disease).build();
        String json =  JsonFormat.printer().print(p1);
        //System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(JSON_REQUIRED, error.type());
        // this error means that the first disease lacks a term
        assertEquals("$.diseases[0].term: is missing but it is required", error.message());
    }



}
