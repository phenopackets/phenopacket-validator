package org.phenopackets.validator.builder;

import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.validator.builder.util.MetaDataUtil;


/**
 * Build the simplest possible phenopacket for validation
 */
public class SimplePhenopacket {

    private final Phenopacket phenopacket;

    public SimplePhenopacket() {
        MetaData simplMetaData = MetaDataUtil.simpleHpoMetaData();
        phenopacket = Phenopacket.newBuilder()
        .setId("hello world")
        .setMetaData(simplMetaData)
        .build();
    }

   
    public Phenopacket getPhenopacket() {
        return phenopacket;
    }


}
