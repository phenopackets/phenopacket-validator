package org.phenopackets.validator.core;

record ValidationItemDefault(ValidatorInfo validatorInfo, ValidationItemType type, String message) implements ValidationItem {}
