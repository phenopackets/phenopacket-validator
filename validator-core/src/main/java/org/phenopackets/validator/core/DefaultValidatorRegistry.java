package org.phenopackets.validator.core;


import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultValidatorRegistry implements PhenopacketValidatorRegistry {

    private final Map<ValidatorInfo, ? extends PhenopacketValidator> validatorMap;

    public DefaultValidatorRegistry(Map<ValidatorInfo, ? extends PhenopacketValidator> validMap) {
        validatorMap = Map.copyOf(validMap);
    }

    @Override
    public Optional<? extends PhenopacketValidator> getValidatorForType(ValidatorInfo type) {
        return Optional.ofNullable(validatorMap.get(type));
    }

    @Override
    public Set<ValidatorInfo> getValidationTypeSet() {
        return validatorMap.keySet();
    }
}
