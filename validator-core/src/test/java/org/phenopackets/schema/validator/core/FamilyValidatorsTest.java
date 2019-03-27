package org.phenopackets.schema.validator.core;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.v1.Family;
import org.phenopackets.schema.v1.core.Pedigree;
import org.phenopackets.schema.v1.core.Sex;
import org.phenopackets.schema.validator.core.examples.RareDiseaseFamilyExample;
import org.phenopackets.schema.validator.core.examples.TestExamples;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

class FamilyValidatorsTest {

    // -------------------------------- checkThatFamilyHasId ----------------------------------------------------

    @Test
    void passWhenFamilyHasNonEmptyId() {
        Family f = Family.newBuilder().setId("ID").build();

        ValidationResult result = FamilyValidators.checkThatFamilyHasId().validate(f);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenFamilyHasEmptyId() {
        Family f = Family.getDefaultInstance();

        ValidationResult result = FamilyValidators.checkThatFamilyHasId().validate(f);

        assertThat(result, is(ValidationResult.fail("Family must have id")));
    }


    // -------------------------------- checkThatProbandIsNotEmpty ----------------------------------------------------

    @Test
    void passWhenProbandIsNotEmpty() {
        Family f = Family.newBuilder().setProband(TestExamples.getJohnnyPhenopacket()).build();

        ValidationResult result = FamilyValidators.checkThatProbandIsNotEmpty().validate(f);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenProbandIsEmpty() {
        Family f = Family.getDefaultInstance();

        ValidationResult result = FamilyValidators.checkThatProbandIsNotEmpty().validate(f);

        assertThat(result, is(ValidationResult.fail("Family must have proband")));
    }

    // -------------------------------- checkThatProbandIsNotEmpty ----------------------------------------------------

    @Test
    void passWhenPedigreeIsPresent() {
        Family f = RareDiseaseFamilyExample.rareDiseaseFamily();

        ValidationResult result = FamilyValidators.checkThatPedigreeIsPresent().validate(f);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenPedigreeIsMissing() {
        Family f = Family.getDefaultInstance();

        ValidationResult result = FamilyValidators.checkThatPedigreeIsPresent().validate(f);

        assertThat(result, is(ValidationResult.fail("Pedigree must be present")));
    }


    // -------------------------------- checkThatProbandIsNotEmpty ----------------------------------------------------

    @Test
    void passWhenProbandIsPresentInPedigree() {
        Family f = RareDiseaseFamilyExample.rareDiseaseFamily();

        ValidationResult result = FamilyValidators.checkThatProbandIsPresentInPedigree().validate(f);

        assertThat(result, is(ValidationResult.pass()));
    }

    @Test
    void failWhenProbandIsNotRepresentedInPedigree() {
        Family f = Family.newBuilder()
                .setProband(TestExamples.getJohnnyPhenopacket()) // has id 'Johnny'
                .setPedigree(Pedigree.newBuilder()
                        .addPersons(Pedigree.Person.newBuilder()
                                .setFamilyId("FAM0123").setIndividualId("Donna").setPaternalId("0").setMaternalId("0")
                                .setSex(Sex.FEMALE).setAffectedStatus(Pedigree.Person.AffectedStatus.UNAFFECTED).build())
                        .build())
                .build();

        ValidationResult result = FamilyValidators.checkThatProbandIsPresentInPedigree().validate(f);

        assertThat(result, is(ValidationResult.fail("Proband with id 'Johnny' is not represented in pedigree")));
    }

    @Test
    void failWhenProbandIsNotPresentInFamily() {
        Family f = Family.getDefaultInstance();

        ValidationResult result = FamilyValidators.checkThatProbandIsPresentInPedigree().validate(f);

        assertThat(result, is(ValidationResult.fail("Proband has not been defined for family")));
    }


    // -------------------------------- checkThatProbandIsNotEmpty ----------------------------------------------------

    @Test
    void passWhenAllRelativesArePresentInPedigree() {
        Family f = RareDiseaseFamilyExample.rareDiseaseFamily();

        List<ValidationResult> results = FamilyValidators.checkThatRelativesArePresentInPedigree().validate(f);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void passWhenNoRelativesAreSpecified() {
        // this family has a proband Phenopacket and also a corresponding Person in Pedigree
        Family f = TestExamples.getSingleSampleFamilyForJohnny();

        List<ValidationResult> results = FamilyValidators.checkThatRelativesArePresentInPedigree().validate(f);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void failWhenRelativeIsNotPresentInPedigree() {
        Family f = TestExamples.getSingleSampleFamilyForJohnny().toBuilder()
                .addRelatives(TestExamples.getDonnaPhenopacket())
                .build();

        List<ValidationResult> results = FamilyValidators.checkThatRelativesArePresentInPedigree().validate(f);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail("Relative 'Donna' is not present in pedigree")));
    }


    // -------------------------------- checkThatPedigreeMembersInfoIsPresent ----------------------------------------------------

    @Test
    void passWhenAllPedigreeMembersAreRepresentedAsPhenopackets() {
        Family f = RareDiseaseFamilyExample.rareDiseaseFamily();

        List<ValidationResult> results = FamilyValidators.checkThatPedigreeMembersInfoIsPresent().validate(f);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void passWithEmptyPedigree() {
        Family f = Family.getDefaultInstance();

        List<ValidationResult> results = FamilyValidators.checkThatPedigreeMembersInfoIsPresent().validate(f);

        assertThat(results.isEmpty(), is(true));
    }

    @Test
    void failWhenPhenopacketIsMissingForAPedigreeMember() {
        String indId = "Donna";
        Family f = TestExamples.getSingleSampleFamilyForJohnny();
        Pedigree p = f.getPedigree().toBuilder()
                .addPersons(Pedigree.Person.newBuilder()
                        .setFamilyId("FAM001")
                        .setIndividualId(indId)
                        .setPaternalId("0")
                        .setMaternalId("0")
                        .setSex(Sex.FEMALE)
                        .setAffectedStatus(Pedigree.Person.AffectedStatus.UNAFFECTED)
                        .build()).build();
        f = f.toBuilder().setPedigree(p).build();

        List<ValidationResult> results = FamilyValidators.checkThatPedigreeMembersInfoIsPresent().validate(f);

        assertThat(results.size(), is(1));
        assertThat(results, hasItem(ValidationResult.fail(String.format("Phenopacket describing pedigree member '%s' is missing", indId))));
    }
}