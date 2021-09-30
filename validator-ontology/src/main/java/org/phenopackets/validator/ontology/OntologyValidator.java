package org.phenopackets.validator.ontology;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.validator.core.PhenopacketValidator;

import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

public abstract class OntologyValidator implements PhenopacketValidator  {
    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyValidator.class);

    protected final static String ONTOLOGY_VALIDATOR = "Ontology validation";

    protected final ValidatorInfo validatorInfo;
    protected final Ontology ontology;

    protected final static String ONTOLOGY_VALIDATOR = "Otology validation";

    protected OntologyValidator(Ontology ontology, ValidatorInfo vinfo) {
        this.ontology = ontology;
        this.validatorInfo = vinfo;
    }

    protected void checkTermsArePresentInOntology(Iterable<TermId> termIds, List<ValidationItem> errors) {
        for (TermId termId : termIds) {
            if (!ontology.containsTerm(termId)) {
                String message = String.format("Could not find term id: %s", termId.getValue());
                ValidationItem item = ValidationItem.of(validatorInfo, OntologyValidationItemTypes.ONTOLOGY_INVALID_ID, message);
                errors.add(item);
            }
        }
    }

    protected void checkTermsUsePrimaryId(Iterable<TermId> termIds, List<ValidationItem> errors) {
        for (TermId termId : termIds) {
            TermId primaryId = ontology.getPrimaryTermId(termId);
            if (primaryId == null)
                // not in ontology, this is checked in `checkTermsArePresentInOntology`
                continue;
            if (!primaryId.equals(termId)) {
                String message = String.format("Using alternate (obsolete) id (%s) instead of primary id (%s)", termId.getValue(), primaryId.getValue());
                ValidationItem item = ValidationItem.of(validatorInfo, OntologyValidationItemTypes.ONTOLOGY_TERM_WITH_ALTERNATE_ID, message);
                errors.add(item);
            }
        }
    }

    protected void checkAncestry(Set<TermId> observedTerms, Set<TermId> excludedTerms, List<ValidationItem> errors) {
        /*
        OBSERVED TERMS:
        It is an error to:
         - provide term and its ancestor,
         - provide term while the ancestor is excluded.
         */
        for (TermId term : observedTerms) {
            Set<TermId> ancestors = ontology.getAncestorTermIds(term);

            for (TermId ancestor : ancestors) {
                if (ancestor.equals(term))
                    continue;

                if (observedTerms.contains(ancestor)) {
                    String message = String.format("Using both term %s and its ancestor %s", term.getValue(), ancestor.getValue());
                    errors.add(ValidationItem.of(validatorInfo, OntologyValidationItemTypes.TERM_AND_ANCESTOR_ARE_USED, message));
                }

                if (excludedTerms.contains(ancestor)) {
                    String message = String.format("Term %s is present while its ancestor %s is excluded", term.getValue(), ancestor.getValue());
                    errors.add(ValidationItem.of(validatorInfo, OntologyValidationItemTypes.TERM_IS_USED_AND_ANCESTOR_IS_EXCLUDED, message));
                }
            }
        }

        /*
        EXCLUDED TERMS:
        It is an error to provide term and its ancestor. It is, however, OK to provide term while the ancestor is present,
        i.e. `Arachnodactyly` is excluded, while `Long fingers` are present.
         */
        for (TermId term : excludedTerms) {
            Set<TermId> ancestors = ontology.getAncestorTermIds(term);

            for (TermId ancestor : ancestors) {
                if (ancestor.equals(term))
                    continue;

                if (excludedTerms.contains(ancestor)) {
                    String message = String.format("Using both term %s and its ancestor %s", term.getValue(), ancestor.getValue());
                    errors.add(ValidationItem.of(validatorInfo, OntologyValidationItemTypes.TERM_AND_ANCESTOR_ARE_USED, message));
                }
            }
        }
    }
}
