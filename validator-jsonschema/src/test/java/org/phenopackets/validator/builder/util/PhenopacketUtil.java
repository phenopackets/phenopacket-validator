package org.phenopackets.validator.builder.util;

import org.phenopackets.schema.v2.core.Resource;



import org.phenopackets.schema.v2.core.ExternalReference;


public class PhenopacketUtil {
    public static final String SCHEMA_VERSION = "2.0";

    public static ExternalReference externalReference(String id, String description) {
        return ExternalReference.newBuilder().setId(id).setDescription(description).build();
    }


    public static Resource resource(String id, String name, String nsPrefix, String iriPrefix, String url, String version) {
        return Resource.newBuilder()
        .setId(id)
        .setName(name)
        .setNamespacePrefix(nsPrefix)
        .setIriPrefix(iriPrefix)
        .setUrl(url)
        .setVersion(version)
        .build();
    }
}
