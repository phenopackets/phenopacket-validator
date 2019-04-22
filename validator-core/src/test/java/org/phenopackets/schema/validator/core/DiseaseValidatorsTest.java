package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.core.Disease;
import org.phenopackets.schema.v1.core.OntologyClass;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class DiseaseValidatorsTest {

    @Test
    void failsWhenTermIsAbsent() {
        Disease d = Disease.getDefaultInstance();
        List<ValidationResult> results = DiseaseValidators.checkTermIsPresent().validate(d);

        assertThat(results.size(), is(3));
        assertThat(results, hasItems(ValidationResult.fail("Ontology class must not be empty"),
                ValidationResult.fail("Id string must not be empty"),
                ValidationResult.fail("Label string must not be empty")
        ));
    }

    @Test
    void passesWhenTermIsPresentAndWellFormatted() {
        Disease d = Disease.newBuilder()
                .setTerm(OntologyClass.newBuilder()
                        .setId("MONDO:0007043")
                        .setLabel("Pfeiffer syndrome").build())
                .build();

        List<ValidationResult> results = DiseaseValidators.checkTermIsPresent().validate(d);

        assertThat(results.size(), is(0));
    }

    @Test
    void failsWhenDiseaseIsEmpty() {
        Disease d = Disease.getDefaultInstance();

        ValidationResult result = DiseaseValidators.checkNotEmpty().validate(d);
        assertThat(result, is(ValidationResult.fail("Disease must not be empty")));
    }

    @Test
    void passesWhenDiseaseIsPresent() {
        Disease d = Disease.newBuilder().setTerm(OntologyClass.newBuilder().build()).build();

        ValidationResult result = DiseaseValidators.checkNotEmpty().validate(d);
        assertThat(result, is(ValidationResult.pass()));
    }
}