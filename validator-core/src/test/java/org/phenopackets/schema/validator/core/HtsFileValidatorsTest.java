package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.core.File;
import org.phenopackets.schema.v1.core.HtsFile;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

class HtsFileValidatorsTest {


    @Test
    void failsIfGenomeAssemblyIsEmpty() {
        HtsFile hf = HtsFile.getDefaultInstance();
        ValidationResult result = HtsFileValidators.genomeAssemblyIsPresent().validate(hf);
        assertThat(result, is(ValidationResult.fail("Genome assembly must not be empty")));
    }


    @Test
    void passesWhenGenomeAssemblySpecified() {
        HtsFile hf = TestExamples.getGVCFForJohnny();
        ValidationResult result = HtsFileValidators.genomeAssemblyIsPresent().validate(hf);
        assertThat(result, is(ValidationResult.pass()));
    }


    @Test
    void failsWhenFileIsMissing() {
        File file = File.getDefaultInstance();
        ValidationResult result = HtsFileValidators.fileIsValid().validate(file);
        assertThat(result, is(ValidationResult.fail("At least one of 'path' and 'uri' must be specified")));
    }


    @Test
    void passesWhenPathOrUriIsSpecified() {
        File pathSet = File.newBuilder().setPath("/some/path/to/file.vcf")
                .setDescription("Some file")
                .build();
        ValidationResult result = HtsFileValidators.fileIsValid().validate(pathSet);
        assertThat(result, is(ValidationResult.pass()));

        File uriSet = File.newBuilder().setUri("https://opensnp.org/data/60.23andme-exome-vcf.231?1341012444")
                .setDescription("Some file")
                .build();
        result = HtsFileValidators.fileIsValid().validate(uriSet);
        assertThat(result, is(ValidationResult.pass()));
    }


    @Test
    void failsWhenFileIsMissingInHtsFile() {
        HtsFile hf = HtsFile.getDefaultInstance();
        ValidationResult result = HtsFileValidators.fileIsPresentAndValid().validate(hf);
        assertThat(result, is(ValidationResult.fail("File must not be empty")));
    }


    @Test
    void passesWhenFileIsPresent() {
        HtsFile hf = TestExamples.getGVCFForJohnny();
        ValidationResult result = HtsFileValidators.fileIsPresentAndValid().validate(hf);
        assertThat(result, is(ValidationResult.pass()));
    }


    @Test
    void warningIsIssuedOnEmptyIndividualToSampleIdMap() {
        HtsFile hf = HtsFile.getDefaultInstance();
        ValidationResult result = HtsFileValidators.individualToSampleIdentifiersArePresent().validate(hf);
        assertThat(result, is(ValidationResult.warn("Map of individual to sample ids is empty")));
    }


    @Test
    void passesWhenIndividualToSampleIdIsNotEmpty() {
        HtsFile hf = TestExamples.getGVCFForJohnny();
        ValidationResult result = HtsFileValidators.individualToSampleIdentifiersArePresent().validate(hf);
        assertThat(result, is(ValidationResult.pass()));
    }
}