package org.phenopackets.validator.ontology;

import com.google.protobuf.util.JsonFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;




public class HpoValidator extends OntologyValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(HpoValidator.class);

    private static final String HPO_PREFIX = "HP";

    public HpoValidator(File jsonFile) {
        super(jsonFile, ValidatorInfo.of(ONTOLOGY_VALIDATOR, "Human Phenotype Ontology"));
    }

    @Override
    public ValidatorInfo info() {
        return validatorInfo;
    }



    /**
     * The logic of this validator is to check the Phenotypic features for HPO terms. If HPO terms
     * are used there, the we check that the HPO terms are included in the ontology (thus, we check for
     * invalid HPO ids, and also check if the id used is the latest or if it is an alternate id). If there
     * are both observed and excluded terms, we check that there is no logical inconsistency, e.g.,
     * NOT Abnormal liver morphology but OBSERVED Hepatic fibrosis. The latter is not possible because
     * Hepatic fibrosis implies Abnormal liver morphology
     * @param inputStream -- stream from the phenopacket.
     * @return list of validation errors
     */
    @Override
    public List<ValidationItem> validate(InputStream inputStream) {
        List<ValidationItem> errors = new ArrayList<>();
        JSONParser parser = new JSONParser();
        Set<TermId> observedFeatures = new HashSet<>();
        Set<TermId> excludedFeatures = new HashSet<>();
        try {
            Object obj = parser.parse(new InputStreamReader(inputStream));
            JSONObject jsonObject = (JSONObject) obj;
            String phenopacketJsonString = jsonObject.toJSONString();
            Phenopacket.Builder phenoPacketBuilder = Phenopacket.newBuilder();
            JsonFormat.parser().merge(phenopacketJsonString, phenoPacketBuilder);
            Phenopacket phenopacket = phenoPacketBuilder.build();
            for (var pf : phenopacket.getPhenotypicFeaturesList()) {
                String id = pf.getType().getId();
                try {
                    TermId tid = TermId.of(id);
                    if (!tid.getPrefix().equals(HPO_PREFIX)) {
                        continue; // only check HPO terms
                    }
                    if (pf.getExcluded()) {
                        excludedFeatures.add(tid);
                    } else {
                        observedFeatures.add(tid);
                    }
                } catch (PhenolRuntimeException pe) {
                    LOGGER.error("Invalid Ontology term id: " + id);
                }
            }
            // 1 check that all terms are in the ontology and use the primary id
            for (TermId tid: observedFeatures) {
                if (!ontology.containsTerm(tid)) {
                    String message = String.format("Could not find term id: %s", tid.getValue());
                    ValidationItem vitem = OntologyValidationItem.invalidTermId(validatorInfo,message);
                    errors.add(vitem);
                } else {
                    TermId primaryId = ontology.getPrimaryTermId(tid);
                    if (! primaryId.equals(tid)) {
                        String message = String.format("Using alternate (obsolete) id (%s) instead of primary id (%s)",
                                tid.getValue(), primaryId);
                        ValidationItem vitem = OntologyValidationItem.invalidTermId(validatorInfo,message);
                        errors.add(vitem);
                    }
                }
            }
            return errors;
        } catch (IOException | RuntimeException | ParseException e) {
            LOGGER.warn("Error while validating: {}", e.getMessage());
        }
        return errors;
    }
}
