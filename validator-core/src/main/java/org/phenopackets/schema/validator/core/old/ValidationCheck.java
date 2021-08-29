package org.phenopackets.schema.validator.core.old;

import com.google.protobuf.Message;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@FunctionalInterface @Deprecated
public interface ValidationCheck<T extends Message> {

    public ValidationResult validate(T message);
}
