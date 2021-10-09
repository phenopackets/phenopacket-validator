package org.phenopackets.validator.jsonschema;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Test;
import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.EvidenceBuilder;
import org.phenopackets.phenotools.builder.builders.PhenotypicFeatureBuilder;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.Evidence;
import org.phenopackets.schema.v2.core.PhenotypicFeature;
import org.phenopackets.validator.core.ValidationItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.phenopackets.validator.jsonschema.JsonValidationItemTypes.JSON_REQUIRED;


public class PhenotypicFeatureValidatorTest extends JsonSchemaValidatorTestBase {
    private static final String PMID = "PMID:30808312";
    private static final String publication = "COL6A1 mutation leading to Bethlem myopathy with recurrent hematuria: a case report";

    private static Phenopacket phenopacketWithOnePhenotypicFeature() {
        var authorAssertion = EvidenceBuilder.authorStatementEvidence(PMID, publication);
        var VSD =
                PhenotypicFeatureBuilder.create("HP:0001629","Ventricular septal defect")
                        .congenitalOnset()
                        .evidence(authorAssertion)
                        .build();
        return PhenopacketBuilder.create("ID:A", metadataWithHpo)
                .phenotypicFeature(VSD)
                .build();
    }

    private final Phenopacket phenopacket = phenopacketWithOnePhenotypicFeature();

    @Test
    public void testPhenotypicFeatureWithoutOntologyTerm() throws InvalidProtocolBufferException {
        assertTrue(phenopacket.getPhenotypicFeaturesCount() > 0);
        PhenotypicFeature pf = phenopacket.getPhenotypicFeatures(0);
        pf = PhenotypicFeature.newBuilder(pf).clearType().build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setPhenotypicFeatures(0, pf).build();
        String json =  JsonFormat.printer().print(p1);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(JSON_REQUIRED, error.type());
        String expectedErrorMsg = "$.phenotypicFeatures[0].type: is missing but it is required";
        assertEquals(expectedErrorMsg, error.message());
    }

    /**
     * This Phenopacket is invalid, because the PhenotypicFeature has an Evidence element that is lacking
     * evidenceCode
     */
    @Test
    public void testPhenotypicFeatureWithInvalidEvidence() throws InvalidProtocolBufferException {
        assertTrue(phenopacket.getPhenotypicFeaturesCount() > 0);
        PhenotypicFeature pf = phenopacket.getPhenotypicFeatures(0);
        assertTrue(pf.getEvidenceCount() > 0);
        Evidence evi = pf.getEvidence(0);
        evi = Evidence.newBuilder(evi).clearEvidenceCode().build();
        pf = PhenotypicFeature.newBuilder(pf).setEvidence(0, evi).build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setPhenotypicFeatures(0, pf).build();
        String json =  JsonFormat.printer().print(p1);
       // System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertEquals(1, errors.size());
        ValidationItem error = errors.get(0);
        assertEquals(JSON_REQUIRED, error.type());
        String expectedErrorMsg = "$.phenotypicFeatures[0].evidence[0].evidenceCode: is missing but it is required";
        assertEquals(expectedErrorMsg, error.message());
    }


}
