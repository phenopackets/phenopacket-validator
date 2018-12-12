package org.phenopackets.schema.validator.core.hpo;

import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.validator.core.ValidationResult;
import org.phenopackets.schema.validator.core.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for validating HPO terms in a {@link org.phenopackets.schema.v1.PhenoPacket}.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class HpoValidator implements Validator<PhenoPacket> {

    private static final Logger logger = LoggerFactory.getLogger(HpoValidator.class);

    public HpoValidator() {
//        this.ontology=ontology;
    }


    @Override
    public ValidationResult validate(PhenoPacket phenoPacket) {
        logger.info("validating HPO terms in phenopacket...");
        List<Validator<PhenoPacket>> validationChecks = new ArrayList<>();
        validationChecks.add(checkExistenceOfMetadata());

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

    private Validator<PhenoPacket> alwaysFail() {
        return it -> ValidationResult.fail("Always fails");
    }

}
