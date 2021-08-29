package org.phenopackets.schema.validator.core.old;

import com.google.protobuf.Message;
import org.phenopackets.schema.validator.core.ValidationResult;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@FunctionalInterface @Deprecated
public interface ValidationCheck<T extends Message> {

    public ValidationResult validate(T message);
}
