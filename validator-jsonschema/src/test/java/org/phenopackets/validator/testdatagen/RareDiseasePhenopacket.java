package org.phenopackets.validator.testdatagen;

import org.phenopackets.phenotools.builder.PhenopacketBuilder;
import org.phenopackets.phenotools.builder.builders.FileBuilder;
import org.phenopackets.phenotools.builder.builders.IndividualBuilder;
import org.phenopackets.phenotools.builder.builders.MetaDataBuilder;
import org.phenopackets.phenotools.builder.builders.PhenotypicFeatureBuilder;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.File;
import org.phenopackets.schema.v2.core.Individual;
import org.phenopackets.schema.v2.core.MetaData;

public class RareDiseasePhenopacket extends DatagenBase {


    private final Phenopacket phenopacket;

    public RareDiseasePhenopacket() {
        Individual proband = IndividualBuilder.create("patient 1")
                .dateOfBirth("1998-01-01T00:00:00Z")
                .ageAtLastEncounter("P3Y")
                .male()
                .build();


        var syndactyly = PhenotypicFeatureBuilder.create("HP:0001159", "Syndactyly")
            .congenitalOnset().build();
        var pneumonia = PhenotypicFeatureBuilder.create("HP:0002090", "Pneumonia")
            .childhoodOnset().build();
        var cryptorchidism = PhenotypicFeatureBuilder.create("HP:0000028", "Cryptorchidism")
        .congenitalOnset().build();
        var sinusitis = PhenotypicFeatureBuilder.create("HP:0011109", "Chronic sinusitis")
            .severe().adultOnset().build();

        MetaData meta = MetaDataBuilder.create("2021-07-01T19:32:35Z", "anonymous biocurator")
                .submittedBy("anonymous submitter")
                .hpWithVersion("2021-08-02")
                .mondoWithVersion("2021-09-01")
                .externalReference("PMID:20842687",
                        "Severe dystonic encephalopathy without hyperphenylalaninemia associated with an 18-bp deletion within the proximal GCH1 promoter")
                .build();
        File vcf = FileBuilder.hg38vcf("file://data/file.vcf.gz")
            .individualToFileIdentifier("kindred 1A", "SAME000234")
            .build();

        this.phenopacket = PhenopacketBuilder.create("phenopacket-id-1", meta)
            .individual(proband)
            .phenotypicFeature(syndactyly)
            .phenotypicFeature(pneumonia)
            .phenotypicFeature(cryptorchidism)
            .phenotypicFeature(sinusitis)
            .file(vcf)
            .build();

    }

    public Phenopacket getPhenopacket() {
        return phenopacket;
    }


    
}
