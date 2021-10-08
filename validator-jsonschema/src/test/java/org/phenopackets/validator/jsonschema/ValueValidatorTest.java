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

import static org.junit.jupiter.api.Assertions.*;
import static org.phenopackets.phenotools.builder.builders.OntologyClassBuilder.ontologyClass;

/**
 *
 * Test a few errors in the Value messages
 *  @author Peter N Robinson
 */
public class ValueValidatorTest {
    private static final Map<ValidatorInfo, PhenopacketValidator> jsonValidatorMap = JsonSchemaValidators.genericValidator();

    private static Phenopacket phenopacketWithValueAndComplexValue() {
        MetaData meta = MetaDataBuilder.create("2021-07-01T19:32:35Z", "anonymous biocurator")
                .submittedBy("anonymous submitter")
                .hpWithVersion("2021-08-02")
                .mondoWithVersion("2021-09-01")
                .build();
        // Simple value for blood platelets
        OntologyClass cellsPerMl = ontologyClass("UO:0000316", "cells per microliter");
        ReferenceRange rrange = ReferenceRangeCreator.create(cellsPerMl, 150_000, 450_000);
        Quantity quantity = QuantityBuilder.create(cellsPerMl,24000)
                .referenceRange(rrange)
                .build();
        Value value = ValueBuilder.create(quantity).build();
        OntologyClass plateletAssay = ontologyClass( "LOINC:26515-7", "Platelets [#/volume] in Blood");
        Measurement plateletM = MeasurementBuilder.value(plateletAssay, value).build();
        return PhenopacketBuilder.create("id:A", meta)
                .measurement(plateletM)
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
        //System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e : errors) {
            System.out.println(e.message());
        }
        assertTrue(errors.isEmpty());
    }

    /**
     * Test what happens if we remove the ontology class from the Measurement
     * Note that the validation tool reports two errors
     * $.measurements[0].value: is missing but it is required
     * $.measurements[0].complexValue: is missing but it is required
     * The second of which is incorrect but I think this is more a limitation of JSON Schema validation
     * of oneOf elements
     */
    @Test
    public void testMissingOntologyTerm() throws InvalidProtocolBufferException {
        PhenopacketValidator validator = jsonValidatorMap.values().stream()
                .findFirst()
                .get();
        Measurement m1 = Phenopacket.newBuilder(phenopacket).getMeasurements(0);
        m1 = Measurement.newBuilder(m1).clearValue().build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setMeasurements(0, m1).build();
        String json =  JsonFormat.printer().print(p1);
        System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        for (var e : errors) {
            System.out.println(e.message());
        }
        assertFalse(errors.isEmpty());
        String expectedErrorMsg = "$.measurements[0].value: is missing but it is required";
        boolean foundError = errors.stream().map(ValidationItem::message).anyMatch(m -> m.equals(expectedErrorMsg));
        assertTrue(foundError, "was expecting to find \"$.measurements[0].value: is missing but it is required\" but did not");
    }

    @Test
    public void referenceRangeMissingLowValue() throws InvalidProtocolBufferException {
        PhenopacketValidator validator = jsonValidatorMap.values().stream()
                .findFirst()
                .get();
        Measurement m1 = Phenopacket.newBuilder(phenopacket).getMeasurements(0);
        Value v1 = m1.getValue();
        ReferenceRange rrange = v1.getQuantity().getReferenceRange();
        rrange = ReferenceRange.newBuilder(rrange).clearLow().build();
        Quantity q1= Quantity.newBuilder(v1.getQuantity()).setReferenceRange(rrange).build();
        v1 = Value.newBuilder(v1).setQuantity(q1).build();
        m1 = Measurement.newBuilder(m1).setValue(v1).build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setMeasurements(0, m1).build();
        String json =  JsonFormat.printer().print(p1);
        //System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
        assertEquals(1, errors.size());
        String expectedErrorMsg = "$.measurements[0].value.quantity.referenceRange.low: is missing but it is required";
        assertEquals(expectedErrorMsg, errors.get(0).message());
    }

    @Test
    public void measurementMissingAssay() throws InvalidProtocolBufferException {
        PhenopacketValidator validator = jsonValidatorMap.values().stream()
                .findFirst()
                .get();
        Measurement m1 = Phenopacket.newBuilder(phenopacket).getMeasurements(0);
        m1 = Measurement.newBuilder(m1).clearAssay().build();
        Phenopacket p1 = Phenopacket.newBuilder(phenopacket).setMeasurements(0, m1).build();
        String json =  JsonFormat.printer().print(p1);
        //System.out.println(json);
        List<? extends ValidationItem> errors = validator.validate(json);
//        for (var e : errors) {
//            System.out.println(e.message());
//        }
        assertEquals(1, errors.size());
        String expectedErrorMsg = "$.measurements[0].assay: is missing but it is required";
        assertEquals(expectedErrorMsg, errors.get(0).message());
    }



}
