package org.phenopackets.validator.jsonschema;

import org.phenopackets.phenotools.builder.builders.MetaDataBuilder;
import org.phenopackets.phenotools.builder.builders.Resources;
import org.phenopackets.phenotools.builder.exceptions.PhenotoolsRuntimeException;
import org.phenopackets.schema.v2.core.MetaData;
import org.phenopackets.validator.core.PhenopacketValidator;
import org.phenopackets.validator.core.ValidatorInfo;

import java.util.Map;

public class JsonSchemaValidatorTestBase {
    private static final Map<ValidatorInfo, PhenopacketValidator> genericValidatorMap = JsonSchemaValidators.genericValidator();
    private static final Map<ValidatorInfo, PhenopacketValidator> rareHpoValidatorMap = JsonSchemaValidators.rareHpoValidator();

    protected final PhenopacketValidator validator = genericValidatorMap.values().stream()
            .findFirst()
            .orElseThrow(() -> new PhenotoolsRuntimeException("Could not retrieve generic validator"));

    protected final PhenopacketValidator rareDiseaseValidator = rareHpoValidatorMap.values().stream()
            .findFirst()
            .orElseThrow(() -> new PhenotoolsRuntimeException("Could not retrieve rare disease validator"));

    protected static final MetaData metadataWithHpo = MetaDataBuilder.create("2021-07-01T19:32:35Z", "anonymous biocurator")
            .submittedBy("anonymous submitter")
            .resource(Resources.hpoVersion("2021-08-02"))
            .resource(Resources.mondoVersion("2021-09-01"))
            .externalReference("PMID:20842687",
                    "Severe dystonic encephalopathy without hyperphenylalaninemia associated with an 18-bp deletion within the proximal GCH1 promoter")
            .build();



}
