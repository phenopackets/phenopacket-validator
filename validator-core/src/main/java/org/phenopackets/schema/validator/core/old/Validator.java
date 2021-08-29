package org.phenopackets.schema.validator.core.old;

import com.google.protobuf.Message;
import org.phenopackets.schema.validator.core.ValidationResult;

import java.util.List;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@FunctionalInterface @Deprecated
public interface Validator<T extends Message> {

    public List<ValidationResult> validate(T message);
}
