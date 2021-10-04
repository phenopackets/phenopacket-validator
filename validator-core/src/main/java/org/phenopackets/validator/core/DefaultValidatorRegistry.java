package org.phenopackets.validator.core;


import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

class DefaultValidatorRegistry implements PhenopacketValidatorRegistry {

    private final Map<ValidatorInfo, ? extends PhenopacketValidator> validatorMap;

    DefaultValidatorRegistry(Map<ValidatorInfo, ? extends PhenopacketValidator> validMap) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultValidatorRegistry that = (DefaultValidatorRegistry) o;
        return Objects.equals(validatorMap, that.validatorMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validatorMap);
    }
}
