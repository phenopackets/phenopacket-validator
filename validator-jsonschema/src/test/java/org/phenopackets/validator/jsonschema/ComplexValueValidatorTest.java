package org.phenopackets.validator.jsonschema;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Test;
import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.*;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.*;
import org.phenopackets.validator.core.ValidationItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.phenopackets.phenotools.builder.builders.OntologyClassBuilder.ontologyClass;

/**
 * Test a few errors in the Value and ComplexValue messages
 * @author Peter N Robinson
 */
public class ComplexValueValidatorTest extends JsonSchemaValidatorTestBase{

    private static Phenopacket phenopacketWithValueAndComplexValue() {
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
        return PhenopacketBuilder.create("id:A", metadataWithHpo)
                .measurement(bpM)
                .build();
    }

    private static final Phenopacket phenopacket = phenopacketWithValueAndComplexValue();

    /**
     * First test that the unaltered phenopacket is OK
     */
    @Test
    public void validatePhenopacket() throws InvalidProtocolBufferException {
        String json =  JsonFormat.printer().print(phenopacket);
        //System.out.println(json);
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
        // Alter the first measurement, which is a ComplexValue
        Measurement m1 = phenopacket.getMeasurements(0);
        assertTrue(m1.hasComplexValue());
        m1 = Measurement.newBuilder(m1).clearAssay().build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setMeasurements(0, m1).build();
        String json =  JsonFormat.printer().print(p1);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e : errors) {
            System.out.println(e);
        }
    }

    /**
     * Each measurement requires an ontology term for the assay
     */
    @Test
    public void testComplexValueHasNoTypedValue() throws InvalidProtocolBufferException {
        // Alter the first measurement, which is a ComplexValue
        Measurement m1 = phenopacket.getMeasurements(0);
        assertTrue(m1.hasComplexValue());
        ComplexValue cvale = m1.getComplexValue();
        cvale = ComplexValue.newBuilder(cvale).clearTypedQuantities().build();
        m1 = Measurement.newBuilder(m1).setComplexValue(cvale).build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setMeasurements(0, m1).build();
        String json =  JsonFormat.printer().print(p1);
        //System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e : errors) {
            System.out.println(e);
        }
        assertEquals(1, errors.size());
        String expectedErrorMsg = "$.measurements[0].complexValue.typedQuantities: is missing but it is required";
        assertEquals(expectedErrorMsg, errors.get(0).message());
    }


}
