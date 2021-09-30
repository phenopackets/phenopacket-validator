package org.phenopackets.validator.jsonschema;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.Disease;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.schema.v2.core.Resource;
import org.phenopackets.schema.v2.core.TimeElement;
import org.phenopackets.validator.core.PhenopacketValidator;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;
import org.phenopackets.validator.testdatagen.PhenopacketUtil;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.phenopackets.validator.jsonschema.JsonValidationItemTypes.*;
import static org.phenopackets.validator.testdatagen.PhenopacketUtil.*;

/**
 * This class creates a simple phenopacket with a Disease object and creates some variations with
 * validation errors.
 * @author Peter N Robinson
 */
public class JsonSchemaDiseaseValidatorTest {

    private static final Map<ValidatorInfo, PhenopacketValidator> jsonValidatorMap = JsonSchemaValidators.genericValidator();

    private static Disease mondoDisease() {
        var chagas = ontologyClass("MONDO:0005491", "Chagas cardiomyopathy");
        var nyha3 = ontologyClass("NCIT:C66907", "New York Heart Association Class III");
        var childhood = TimeElement.newBuilder().setOntologyClass(CHILDHOOD_ONSET).build();
        return Disease.newBuilder()
                .setTerm(chagas)
                .setOnset(childhood)
                .addDiseaseStage(nyha3)
                .build();
    }

    private static Phenopacket phenopacketWithDisease() {
        Resource hpo = hpoResource("2021-08-02");
        Resource mondo = mondoResource("2021-09-01");
        MetaData meta = PhenopacketUtil.MetaDataBuilder.create("2021-07-01T19:32:35Z", "anonymous biocurator")
                .submittedBy("anonymous submitter")
                .addResource(hpo)
                .addResource(mondo)
                .addExternalReference("PMID:20842687",
                        "Severe dystonic encephalopathy without hyperphenylalaninemia associated with an 18-bp deletion within the proximal GCH1 promoter")
                .build();
        Disease chagasCardiomyopathy = mondoDisease();
        return Phenopacket.newBuilder()
                .setId("A")
                .addDiseases(chagasCardiomyopathy)
                .setMetaData(meta)
                .build();
    }

    private static final Phenopacket phenopacket = phenopacketWithDisease();

    @Test
    public void testPhenopacketValidity() throws InvalidProtocolBufferException {
        PhenopacketValidator validator = jsonValidatorMap.values().stream()
                .findFirst()
                .get();
        String json =  JsonFormat.printer().print(phenopacket);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertTrue(errors.isEmpty());
    }

    @Test
    public void testLacksId() throws InvalidProtocolBufferException {
        PhenopacketValidator validator = jsonValidatorMap.values().stream()
                .findFirst()
                .get();
        // the Phenopacket is not valid if we remove the id
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).clearId().build();
        String json =  JsonFormat.printer().print(p1);
        System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e: errors) {
            System.out.println(e);
        }
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(JSON_REQUIRED, error.type());
        assertEquals("$.id: is missing but it is required", error.message());
    }



}
