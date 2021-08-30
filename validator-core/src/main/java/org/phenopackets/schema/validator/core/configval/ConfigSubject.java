package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;

public enum ConfigSubject {

    PHENOPACKET_SUBJECT,
    PHENOTYPIC_SUBJECT_TIME_AT_LAST_ENCOUNTER,
    PHENOPACKET_PHENOTYPIC_FEATURES,
    PHENOPACKET_PHENOTYPIC_FEATURES_TYPE;


    public static ConfigSubject stringToEnum(String action) {
        switch (action) {
            case "phenopacket.subject":
                return PHENOPACKET_SUBJECT;
            case "phenopacket.subject.timeAtLastEncounter":
                return PHENOTYPIC_SUBJECT_TIME_AT_LAST_ENCOUNTER;
            case "phenopacket.phenotypicFeatures":
                return PHENOPACKET_PHENOTYPIC_FEATURES;
            case "phenopacket.phenotypicFeatures.type":
                return PHENOPACKET_PHENOTYPIC_FEATURES_TYPE;
            default:
                throw new PhenopacketValidatorRuntimeException("Did not recognize subject \"" + action + "\"");
        }
    }


}
