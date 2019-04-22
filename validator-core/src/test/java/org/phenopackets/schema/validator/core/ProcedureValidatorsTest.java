package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.core.Procedure;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.phenopackets.schema.validator.core.examples.TestExamples.ontologyClass;

class ProcedureValidatorsTest {


    @Test
    void failIfProcedureIsEmpty() {
        Procedure p = Procedure.getDefaultInstance();
        ValidationResult result = ProcedureValidators.checkNotEmpty().validate(p);

        assertThat(result, is(ValidationResult.fail("Procedure must not be empty")));
    }

    @Test
    void passWhenProcedureIsNotEmpty() {
        Procedure p = TestExamples.punchBiopsyOfForearmSkin();
        ValidationResult result = ProcedureValidators.checkNotEmpty().validate(p);

        assertThat(result, is(ValidationResult.pass()));
    }


    // -------------------------------------------- checkCodeIsWellFormatted -------------------------------------------


    @Test
    void failWhenCodeNotWellFormatted() {
        Procedure missingCodeLabel = Procedure.newBuilder().setCode(ontologyClass("NCIT:C28743", "")).build();
        List<ValidationResult> results = ProcedureValidators.checkCodeIsWellFormatted().validate(missingCodeLabel);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Label string must not be empty")));

        Procedure missingCodeId = Procedure.newBuilder().setCode(ontologyClass("", "Punch Biopsy")).build();
        results = ProcedureValidators.checkCodeIsWellFormatted().validate(missingCodeId);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Id string must not be empty")));
    }

    @Test
    void failWhenCodeIsEmpty() {
        Procedure missingCode = Procedure.getDefaultInstance();
        List<ValidationResult> results = ProcedureValidators.checkCodeIsWellFormatted().validate(missingCode);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Procedure code must not be empty")));
    }

    @Test
    void passWhenCodeIsWellFormatted() {
        Procedure p = TestExamples.punchBiopsyOfForearmSkin();
        List<ValidationResult> results = ProcedureValidators.checkCodeIsWellFormatted().validate(p);

        assertThat(results.size(), is(0));
    }


}