package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.phenopackets.schema.v1.core.Age;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

class AgeValidatorsTest {

    // ------------------------------------ ageStringIsWellFormatted ----------------------------

    @ParameterizedTest
    @ValueSource(strings = {"AC", "XG", "125"})
    void failOnGibberishNotStartingWithP(String ageString) {
        Age age = Age.newBuilder().setAge(ageString).build();
        ValidationResult result = AgeValidators.ageStringIsWellFormatted().validate(age);

        assertThat(result, is(ValidationResult.fail(String.format("Missing period designator ('P') in the age '%s'", ageString))));
    }


    @ParameterizedTest
    @ValueSource(strings = {"P1Y", "P1Y2M", "P1Y2M3W", "P1Y2M3W3D", "P0Y", "P0M", "P0W", "P0D"})
    void passWithCorrectDateDesignators(String ageString) {
        Age age = Age.newBuilder().setAge(ageString).build();
        ValidationResult result = AgeValidators.ageStringIsWellFormatted().validate(age);

        assertThat(result, is(ValidationResult.pass()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"P1y", "P1y2m", "P1y2m", "P1y2m3w", "P1y2m3w4d", "PC", "P45s32G", "P2T25K"})
    void failOnInvalidDateDesignators(String ageString) { // invalid case, gibberish after P
        Age age = Age.newBuilder().setAge(ageString).build();
        ValidationResult result = AgeValidators.ageStringIsWellFormatted().validate(age);

        assertThat(result, is(ValidationResult.fail(String.format("Date part of '%s' is invalid", ageString))));
    }


    @ParameterizedTest
    @ValueSource(strings = {"PT2H", "PT2H30M", "PT2H30M25S", "PT0H", "PT0M", "PT0S"})
    void passWithCorrectTimeDesignators(String ageString) {
        Age age = Age.newBuilder().setAge(ageString).build();
        ValidationResult result = AgeValidators.ageStringIsWellFormatted().validate(age);

        assertThat(result, is(ValidationResult.pass()));
    }


    @ParameterizedTest
    @ValueSource(strings = {"P1YT1h", "P1YT1m", "P1YT2s", "PT1h", "PT1m", "PT2s", "P2YT43Y"})
    void failOnInvalidTimeDesignators(String ageString) { // invalid case, gibberish
        Age age = Age.newBuilder().setAge(ageString).build();
        ValidationResult result  = AgeValidators.ageStringIsWellFormatted().validate(age);

        assertThat(result, is(ValidationResult.fail(String.format("Time part of '%s' is invalid", ageString))));
    }


    @Test
    void failWhenNoDateOrTimeElementIsPresent() {
        String ageString = "P";
        Age age = Age.newBuilder().setAge(ageString).build();
        ValidationResult result = AgeValidators.ageStringIsWellFormatted().validate(age);

        assertThat(result, is(
                ValidationResult.fail("You have to use at least one date|time element")));

        ageString = "PT";
         age = Age.newBuilder().setAge(ageString).build();
        result = AgeValidators.ageStringIsWellFormatted().validate(age);

        assertThat(result, is(ValidationResult.fail("You have to use at least one date|time element")));
    }

}