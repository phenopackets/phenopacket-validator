package org.phenopackets.validator.jsonschema;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Test;
import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.*;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.Biosample;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.schema.v2.core.OntologyClass;
import org.phenopackets.validator.core.ValidationItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.phenopackets.phenotools.builder.builders.OntologyClassBuilder.ontologyClass;

public class BiosampleValidatorTest extends JsonSchemaValidatorTestBase {
    private static final String PHENOPACKET_ID = "arbitrary.id";
    private static final String PROBAND_ID = "proband A";
    private static final OntologyClass BIOPSY = ontologyClass("NCIT:C15189", "Biopsy");
    private static MetaData metadata = MetaDataBuilder.create("2021-05-14T10:35:00Z", "anonymous biocurator")
            .ncitWithVersion("21.05d")
            .efoWithVersion("3.34.0")
            .uberonWithVersion("2021-07-27")
            .ncbiTaxonWithVersion(" 2021-06-10")
            .build();
    private static final Biosample lymphNodeBiopsy = BiosampleBuilder.create("biosample 2")
            .individualId(PROBAND_ID)
            .timeOfCollection(TimeElementBuilder.create().age("P48Y3M").build())
            .sampledTissue(ontologyClass("NCIT:C139196", "Esophageal Lymph Node"))
            .tumorProgression(ontologyClass("NCIT:C84509", "Primary Malignant Neoplasm"))
            .histologicalDiagnosis(ontologyClass("NCIT:C4024", "Esophageal Squamous Cell Carcinoma"))
            .diagnosticMarker(ontologyClass("NCIT:C131711", "Human Papillomavirus-18 Positive"))
            .procedure(ProcedureBuilder.create(BIOPSY).build())
            .build();

    private static final Biosample  lungBiopsy = BiosampleBuilder.create("biosample 3")
            .individualId(PROBAND_ID)
            .timeOfCollection(TimeElementBuilder.create().age("P50Y7M").build())
            .sampledTissue(ontologyClass("NCIT:C12468", "Lung"))
            .tumorProgression(ontologyClass("NCIT:C3261", "Metastatic Neoplasm"))
            .procedure(ProcedureBuilder.create(BIOPSY).build())
            .build();

    private static Phenopacket phenopacketWithBiosample() {
        return PhenopacketBuilder.create(PHENOPACKET_ID, metadata)
                .biosample(lymphNodeBiopsy)
                .build();
    }

    private static final Phenopacket phenopacket = phenopacketWithBiosample();

    @Test
    public void testValidityOfBiosample() throws InvalidProtocolBufferException {
        String json =  JsonFormat.printer().print(phenopacket);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertTrue(errors.isEmpty());
    }

    /**
     * The only required element in a Biosample is the ID
     */
    @Test
    public void testBiosampleLacksId() throws InvalidProtocolBufferException {
        assertTrue(phenopacket.getBiosamplesCount() == 1);
        Biosample biosample = phenopacket.getBiosamples(0);
        biosample = Biosample.newBuilder(biosample).clearId().build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setBiosamples(0, biosample).build();
        String json =  JsonFormat.printer().print(p1);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertEquals(1, errors.size());
        String expectedErrorMsg = "$.biosamples[0].id: is missing but it is required";
        assertEquals(expectedErrorMsg, errors.get(0).message());
    }



}
