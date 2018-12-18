package org.phenopackets.schema.validator.core;

import com.google.protobuf.Message;
import java.util.List;


/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@FunctionalInterface
public interface Validator<T extends Message> {
    public List<ValidationResult> validate(T message);

}
