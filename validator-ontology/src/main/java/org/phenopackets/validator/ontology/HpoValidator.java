package org.phenopackets.validator.ontology;

import com.google.protobuf.util.JsonFormat;
import org.monarchinitiative.phenol.base.PhenolRuntimeException;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.v2.core.PhenotypicFeature;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidationItemTypes;
import org.phenopackets.validator.core.ValidatorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * The validator checks the Phenotypic features for HPO terms. If HPO terms
 * are used there, then we check that the HPO terms are included in the ontology (thus, we check for
 * invalid HPO ids, and also check if the id used is the latest or if it is an alternate id).
 * <p>
 * The validator performs logical checks too. Due to the annotation propagation rule, where presence of a child term
 * implies presence of all its ancestors, it is an error to specify both a child term and its parent, and only the
 * child term must be used. The rule is checked for both observed and excluded terms.
 * <p>
 * In addition, if there are both observed and excluded terms, we check that there is no logical inconsistency, e.g.,
 * NOT Abnormal liver morphology but OBSERVED Hepatic fibrosis. The latter is not possible because
 * Hepatic fibrosis implies Abnormal liver morphology.
 */
public class HpoValidator extends OntologyValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(HpoValidator.class);

    private static final String HPO_PREFIX = "HP";

    public HpoValidator(Ontology hpoOntology) {
        super(hpoOntology, ValidatorInfo.of(ONTOLOGY_VALIDATOR, "Human Phenotype Ontology"));
    }

    private static Phenopacket readPhenopacket(String phenopacketJsonString) throws IOException {
        Phenopacket.Builder phenoPacketBuilder = Phenopacket.newBuilder();
        JsonFormat.parser().merge(phenopacketJsonString, phenoPacketBuilder);
        return phenoPacketBuilder.build();
    }

    @Override
    public ValidatorInfo info() {
        return validatorInfo;
    }

    @Override
    public List<ValidationItem> validate(String jsonString) {
        Phenopacket phenopacket;
        try {
            phenopacket = readPhenopacket(jsonString);
        } catch (IOException e) {
            LOGGER.warn("Error while validating: {}", e.getMessage(), e);
            return List.of(ValidationItem.of(validatorInfo, ValidationItemTypes.syntaxError(), e.getMessage()));
        }

        List<ValidationItem> errors = new ArrayList<>();
        Set<TermId> observedFeatures = new HashSet<>();
        Set<TermId> excludedFeatures = new HashSet<>();

        partitionTermsToObservedAndExcluded(phenopacket.getPhenotypicFeaturesList(), errors, observedFeatures, excludedFeatures);

        checkTermsArePresentInOntology(observedFeatures, errors);
        checkTermsUsePrimaryId(observedFeatures, errors);

        checkTermsArePresentInOntology(excludedFeatures, errors);
        checkTermsUsePrimaryId(excludedFeatures, errors);

        checkAncestry(observedFeatures, excludedFeatures, errors);

        return errors;
    }

    private void partitionTermsToObservedAndExcluded(Iterable<PhenotypicFeature> phenotypicFeatures,
                                                     List<ValidationItem> errors,
                                                     Set<TermId> observedFeatures,
                                                     Set<TermId> excludedFeatures) {
        for (var pf : phenotypicFeatures) {
            TermId tid;
            String id = pf.getType().getId();
            try {
                tid = TermId.of(id);
                if (!tid.getPrefix().equals(HPO_PREFIX)) {
                    continue; // only check HPO terms
                }
            } catch (PhenolRuntimeException pe) {
                String message = String.format("Invalid ontology term id: %s", id);
                errors.add(ValidationItem.of(validatorInfo, OntologyValidationItemTypes.ONTOLOGY_INVALID_ID, message));
                continue;
            }

            if (pf.getExcluded()) {
                excludedFeatures.add(tid);
            } else {
                observedFeatures.add(tid);
            }
        }
    }
}
