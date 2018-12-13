package org.phenopackets.schema.validator.core.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Phenotype;
import org.phenopackets.schema.validator.core.ValidationResult;
import org.phenopackets.schema.validator.core.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for validating HPO terms in a {@link org.phenopackets.schema.v1.PhenoPacket}.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class HpoValidator implements Validator<PhenoPacket> {

    private static final Logger logger = LoggerFactory.getLogger(HpoValidator.class);

    private final HpoOntology ontology;


    public HpoValidator(HpoOntology ont) {
        this.ontology=ont;
    }


    @Override
    public ValidationResult validate(PhenoPacket phenoPacket) {
        logger.info("validating HPO terms in phenopacket...");
        List<Validator<PhenoPacket>> validationChecks = new ArrayList<>();
        validationChecks.add(checkExistenceOfMetadata());
        validationChecks.add(checkExistenceOfSubject());
        validationChecks.add(checkNonObsolesenceOfHpoTerms());

        for (Validator<PhenoPacket> validationCheck : validationChecks) {
            ValidationResult validationResult = validationCheck.validate(phenoPacket);
            if (!validationResult.isValid()) {
                return validationResult;
            }
        }
        return ValidationResult.pass();
    }

    private Validator<PhenoPacket> checkExistenceOfMetadata() {
        return phenoPacket -> {
            MetaData md = phenoPacket.getMetaData();
            if (md.equals(MetaData.getDefaultInstance())) {
                return ValidationResult.fail("Metadata is empty");
            }
            return ValidationResult.pass();
        };
    }

    /** A valid phenopacket must have a subject. */
    private Validator<PhenoPacket> checkExistenceOfSubject() {
        return phenoPacket -> {
            Individual subject = phenoPacket.getSubject();
            if (subject.equals(Individual.getDefaultInstance())) {
                return ValidationResult.fail("Phenopacket does not have a subject");
            }
            return ValidationResult.pass();
        };
    }

    /** A phenopacket is not allowed to have obsolete terms */
    private Validator<PhenoPacket> checkNonObsolesenceOfHpoTerms() {
        return phenoPacket -> {
            Individual subject =  phenoPacket.getSubject();
            List<Phenotype> phenotypes = subject.getPhenotypesList();
            for (Phenotype pt : phenotypes) {
                OntologyClass term =  pt.getType();
                TermId tid = TermId.of(term.getId());
                if (this.ontology.getObsoleteTermIds().contains(tid)) {
                    return ValidationResult.fail("Phenopacket has an obsolete term with id: " + tid.getValue());
                }
            }
            return ValidationResult.pass();
        };
    }





    private Validator<PhenoPacket> alwaysFail() {
        return it -> ValidationResult.fail("Always fails");
    }

}
