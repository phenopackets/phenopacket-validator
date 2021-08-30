package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;

public enum ConfigAction {

    REQUIRED, AT_LEAST_ONE, SPECIFY_ONTOLOGY;



    public static ConfigAction stringToEnum(String action) {
        switch (action) {
            case "required":
                return REQUIRED;
            case "[1:]":
                return AT_LEAST_ONE;
            default:
                throw new PhenopacketValidatorRuntimeException("Did not recognize action \"" + action + "\"");
        }
    }


}
