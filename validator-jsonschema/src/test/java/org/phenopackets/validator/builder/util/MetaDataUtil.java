package org.phenopackets.validator.builder.util;

import com.google.protobuf.Timestamp;
import org.phenopackets.schema.v2.core.ExternalReference;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.schema.v2.core.Resource;
import org.phenopackets.schema.v2.core.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.phenopackets.validator.builder.util.PhenopacketUtil.externalReference;
import static org.phenopackets.validator.builder.util.PhenopacketUtil.resource;;

public class MetaDataUtil {

    private final static String DEFAULT_PMID =  "PMID:33264411";
    private final static String DEFAULT_TITLE =  "The Human Phenotype Ontology in 2021";
    private final static ExternalReference DEFAULT_EXTERNAL_REFERENCE =
            externalReference(DEFAULT_PMID, DEFAULT_TITLE );
    private final static Resource HPO_RESOURCE = resource("hp", "Human Phenotype Ontology", "HP", "http://purl.obolibrary.org/obo/HP_","http://purl.obolibrary.org/obo/hp.owl","2018-03-08");
    private final static Resource GENO_RESOURCE = resource("geno", "Genotype Ontology", "GENO", "http://purl.obolibrary.org/obo/GENO_", "http://purl.obolibrary.org/obo/geno.owl", "19-03-2018");

    private final static LocalDateTime timeNow = LocalDate.of(2021, 5, 14).atTime(10, 35);
    /**
     * Time when the MetaData was constructed.
     */
    private final static Timestamp timestamp =Timestamp.newBuilder()
            .setSeconds(timeNow.toEpochSecond(ZoneOffset.UTC))
            .build();

    private final MetaData metaData;

    private MetaDataUtil() {
        this(DEFAULT_EXTERNAL_REFERENCE);
    }

    private MetaDataUtil(ExternalReference citation) {
        this.metaData = MetaData.newBuilder()
                .addResources(HPO_RESOURCE)
                .addResources(GENO_RESOURCE)
                .setCreatedBy("Peter R.")
                .setCreated(timestamp)
                .setPhenopacketSchemaVersion("2.0")
                .addExternalReferences(citation)
                .build();
    }

    public static MetaData simpleHpoMetaData() {
        return MetaData.newBuilder()
                .addResources(HPO_RESOURCE)
                .setCreatedBy("Peter R.")
                .setCreated(timestamp)
                .setPhenopacketSchemaVersion("2.0")
                .build();
    }



    public static MetaData defaultRareDiseaseMetaData(ExternalReference externalReference) {
        MetaDataUtil mdatautil = new MetaDataUtil(externalReference);
        return mdatautil.metaData;
    }


    public static Timestamp parseTimestamp(String times) {
        try {
            return com.google.protobuf.util.Timestamps.parse(times);
        } catch (java.text.ParseException pe) {
            throw new RuntimeException(pe.getMessage());
        }
    }


    public static MetaData defaultCancerMetadata() {
        return MetaData.newBuilder()
                .setPhenopacketSchemaVersion(PhenopacketUtil.SCHEMA_VERSION)
                .setCreated(parseTimestamp("2016-06-29T12:03:03.240Z"))
                .addUpdates(Update.newBuilder().setTimestamp(parseTimestamp("2018-06-10T10:59:06.784Z")))
                .addResources(Resource.newBuilder()
                        .setId("pato")
                        .setName("PhenotypicFeature And Trait Ontology")
                        .setNamespacePrefix("PATO")
                        .setUrl("http://purl.obolibrary.org/obo/pato.owl")
                        .setIriPrefix("http://purl.obolibrary.org/obo/PATO_")
                        .setVersion("2018-03-28")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("efo")
                        .setName("Experimental Factor Ontology")
                        .setNamespacePrefix("EFO")
                        .setUrl("http://www.ebi.ac.uk/efo/efo.owl")
                        .setIriPrefix("http://purl.obolibrary.org/obo/EFO_")
                        .setVersion("2.97")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("cl")
                        .setName("Cell Ontology")
                        .setNamespacePrefix("CL")
                        .setUrl("http://purl.obolibrary.org/obo/cl.owl")
                        .setIriPrefix("http://purl.obolibrary.org/obo/CL_")
                        .setVersion("2017-12-11")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("bto")
                        .setName("BRENDA tissue / enzyme source")
                        .setNamespacePrefix("BTO")
                        .setUrl("http://purl.obolibrary.org/obo/bto")
                        .setIriPrefix("http://purl.obolibrary.org/obo/BTO_")
                        .setVersion("2016-05-05")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("uberon")
                        .setName("Uber-anatomy ontology")
                        .setNamespacePrefix("UBERON")
                        .setUrl("http://purl.obolibrary.org/obo/uberon.owl")
                        .setIriPrefix("http://purl.obolibrary.org/obo/UBERON_")
                        .setVersion("2018-05-14")
                        .build())
                .addResources(Resource.newBuilder()
                        .setId("ncbitaxon")
                        .setName("NCBI organismal classification")
                        .setNamespacePrefix("NCBITaxon")
                        .setUrl("http://purl.obolibrary.org/obo/ncbitaxon.owl")
                        .setIriPrefix("http://purl.obolibrary.org/obo/NCBITaxon_")
                        .setVersion("2018-03-02")
                        .build())
                .setCreatedBy("Anonymous biocurator")
                .build();
    }

}