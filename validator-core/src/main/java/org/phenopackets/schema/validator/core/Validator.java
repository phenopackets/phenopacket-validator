package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.PhenoPacket;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@FunctionalInterface
public interface Validator {

    public void validate(PhenoPacket phenoPacket);
}
