package org.phenopackets.schema.validator.core.hpo;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Phenotype;
import org.phenopackets.schema.validator.core.ValidationResult;
import org.phenopackets.schema.validator.core.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class for validating HPO terms in a {@link org.phenopackets.schema.v1.PhenoPacket}.
 * <p>
 * Checks being performed here:
 * <ul>
 *     <li>check that phenopacket does not contain obsolete terms</li>
 *     <li>check that phenopacket does not contain term as well as its ancestor</li>
 * </ul>
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 * @author Daniel Danis <daniel.danis@jax.org>
 */
public class HpoValidator implements Validator<PhenoPacket> {

    private final Ontology ontology;


    public HpoValidator(Ontology ont) {
        this.ontology = ont;
    }


    @Override
    public List<ValidationResult> validate(PhenoPacket message) {
        List<ValidationResult> results = new ArrayList<>();

        results.addAll(checkNonObsolenceOfHpoTerms(message));
        results.addAll(checkTermAncestorsInSubject(message));

        return results;
    }


    /**
     * A phenopacket is not allowed to have obsolete terms
     */
    private List<ValidationResult> checkNonObsolenceOfHpoTerms(PhenoPacket phenoPacket) {
        List<ValidationResult> results = new ArrayList<>();
        Individual subject = phenoPacket.getSubject();
        List<Phenotype> phenotypes = subject.getPhenotypesList();
        for (Phenotype pt : phenotypes) {
            OntologyClass term = pt.getType();
            TermId tid = TermId.of(term.getId());
            if (ontology.getObsoleteTermIds().contains(tid)) {
                results.add(ValidationResult.fail("Phenopacket has an obsolete term with id: " + tid.getValue()));
            }
        }
        return results;
    }


    /**
     * A phenopacket is not allowed to contain phenotype terms together with their ancestors
     */
    private List<ValidationResult> checkTermAncestorsInSubject(PhenoPacket message) {
        List<ValidationResult> results = new ArrayList<>();

        final List<Phenotype> phenotypesList = message.getSubject().getPhenotypesList();
        for (Phenotype outer : phenotypesList) { // for each phenotype term
            final TermId outerTid = TermId.of(outer.getType().getId());
            // get all its ancestors
            final Set<TermId> ancestorTermIds = ontology.getAncestorTermIds(outerTid);
            // and check that any of the ancestors is not present as another term
            for (Phenotype inner : phenotypesList) {
                final TermId innerTid = TermId.of(inner.getType().getId());
                if (ancestorTermIds.contains(innerTid)) {
                    results.add(ValidationResult.fail(String.format("PhenoPacket contains term as well as its ancestor - term: '%s - %s', ancestor: '%s - %s'",
                            outer.getType().getId(), outer.getType().getLabel(), // term
                            inner.getType().getId(), inner.getType().getLabel()))); // ancestor
                }
            }
        }
        return results;
    }
}

////////////////////////////////////////// LEGACY ////////////////////////////////////////////
//    @Override
//    public List<ValidationResult> validate(PhenoPacket phenoPacket) {
//        logger.info("validating HPO terms in phenopacket...");
// TODO: to what extent should this be HPO specific and where should a more general validation occur?
// Things like the MetaData validation ought to be general, yet the HPO-specific steps might be to check the
// validity of individual curies and labels in the OntologyClasses with CURIEs with an HP prefix.
//        return validationChecks.stream()
//                .map(vc -> vc.validate(phenoPacket))
//                .filter(ValidationResult::notValid)
//                .collect(toList());
//    }