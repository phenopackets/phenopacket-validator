package org.phenopackets.validator.jsonschema;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Test;
import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.*;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.*;
import org.phenopackets.validator.core.PhenopacketValidator;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.phenopackets.phenotools.builder.builders.OntologyClassBuilder.ontologyClass;

/**
 * Test a few errors in the Value and ComplexValue messages
 * @author Peter N Robinson
 */
public class ComplexValueValidatorTest {
    private static final Map<ValidatorInfo, PhenopacketValidator> jsonValidatorMap = JsonSchemaValidators.genericValidator();

    private static Phenopacket phenopacketWithValueAndComplexValue() {
        MetaData meta = MetaDataBuilder.create("2021-07-01T19:32:35Z", "anonymous biocurator")
                .submittedBy("anonymous submitter")
                .hpWithVersion("2021-08-02")
                .mondoWithVersion("2021-09-01")
                .build();
        // Simple value for blood platelets
        // Complex value for blood pressure
        OntologyClass systolic = ontologyClass("NCIT:C25298", "Systolic Blood Pressure");
        OntologyClass diastolic = ontologyClass("NCIT:C25299", "Diastolic Blood Pressure");
        OntologyClass mmHg = ontologyClass("NCIT:C49670",  "Millimeter of Mercury");
        ComplexValue complexValue = ComplexValueBuilder.create()
                .typedQuantity(systolic, QuantityBuilder.create(mmHg, 120).build())
                .typedQuantity(diastolic, QuantityBuilder.create(mmHg, 70).build())
                .build();
        OntologyClass bpAssay = ontologyClass("CMO:0000003", "blood pressure measurement");
        Measurement bpM = MeasurementBuilder.complexValue(bpAssay, complexValue).build();
        return PhenopacketBuilder.create("id:A", meta)
                .measurement(bpM)
                .build();
    }

    private static final Phenopacket phenopacket = phenopacketWithValueAndComplexValue();

    /**
     * First test that the unaltered phenopacket is OK
     */
    @Test
    public void validatePhenopacket() throws InvalidProtocolBufferException {
        PhenopacketValidator validator = jsonValidatorMap.values().stream()
                .findFirst()
                .get();
        String json =  JsonFormat.printer().print(phenopacket);
        System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e : errors) {
            System.out.println(e.message());
        }
        assertTrue(errors.isEmpty());
    }

    /**
     * Each measurement requires an ontology term for the assay
     */
    @Test
    public void testMissingAssay() throws InvalidProtocolBufferException {
        PhenopacketValidator validator = jsonValidatorMap.values().stream()
                .findFirst()
                .get();
        // Alter the first measurement, which is a Value
        Measurement m1 = phenopacket.getMeasurements(0);
        assertTrue(m1.hasValue());
        m1 = Measurement.newBuilder(m1).clearAssay().build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setMeasurements(0, m1).build();
        String json =  JsonFormat.printer().print(p1);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e : errors) {
            System.out.println(e);
        }
    }


}
