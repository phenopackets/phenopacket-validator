package org.phenopackets.validator.testdatagen;

import org.phenopackets.phenotools.builder.builders.MetaDataBuilder;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.MetaData;



/**
 * Build the simplest possible phenopacket for validation
 */
public class SimplePhenopacket extends DatagenBase {

    private final Phenopacket phenopacket;

    public SimplePhenopacket() {
        MetaData meta = MetaDataBuilder.create("2021-07-01T19:32:35Z", "anonymous biocurator")
                .submittedBy("anonymous submitter")
                .hpWithVersion("2021-08-02")
                .build();
        phenopacket = Phenopacket.newBuilder()
        .setId("hello world")
        .setMetaData(meta)
        .build();
    }

   
    public Phenopacket getPhenopacket() {
        return phenopacket;
    }


}
