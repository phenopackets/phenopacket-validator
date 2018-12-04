package org.phenopackets.schema.validator.core.hpo;

import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.validator.core.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for validating HPO terms in a {@link org.phenopackets.schema.v1.PhenoPacket}.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class HpoValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(HpoValidator.class);

    @Override
    public void validate(PhenoPacket phenoPacket) {
        logger.info("validating HPO terms in phenopacket...");
    }
}
