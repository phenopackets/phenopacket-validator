package org.phenopackets.schema.validator.core.except;

import org.phenopackets.schema.validator.core.validation.PhenopacketValidator;

public class PhenopacketValidatorRuntimeException extends RuntimeException {

    public PhenopacketValidatorRuntimeException() { super(); }
    public PhenopacketValidatorRuntimeException(String msg) { super(msg); }
}
