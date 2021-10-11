package org.phenopackets.validator.core;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The phenopacket validator registry stores available Validators designed to perform specific {@link ValidatorInfo}.
 * <p>
 * @author Daniel Danis
 * @author Peter N Robinson
 */
public interface PhenopacketValidatorRegistry {

    static PhenopacketValidatorRegistry of(Map<ValidatorInfo, PhenopacketValidator> validMap) {
        return new DefaultValidatorRegistry(validMap);
    }

    Optional<? extends PhenopacketValidator> getValidatorForType(ValidatorInfo type);

    Set<ValidatorInfo> getValidationTypeSet();
}

