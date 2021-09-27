package org.phenopackets.validator.ontology;

import org.phenopackets.validator.core.ValidationItemType;

public class OntologyValidationItemType implements ValidationItemType {


    private final String itemType;
    private final String description;

    /**
     * This error type refers to the use of a term ID, e.g., HP:1234567, which is syntactically correct but which
     * does not existing in the actual ontology.
     */
    public final static OntologyValidationItemType ONTOLOGY_INVALID_ID =
            new OntologyValidationItemType("Invalid Ontology Term ID", "Term id does not exist in ontology");

    /**
     *   This error type refers to the use of a term ID that is an alternate_id, i.e., an outdated ID that should
     *   be replaced by the primary ID of the corresponding Ontology term.
     */
    public final static OntologyValidationItemType ONTOLOGY_TERM_WITH_ALTERNATE_ID =
            new OntologyValidationItemType("Use of outdated Ontology Term ID", "Term id is not the primary id for this term");


    OntologyValidationItemType(String type, String desc) {
        itemType = type;
        description = desc;
    }


    @Override
    public String itemType() {
        return itemType;
    }

    @Override
    public String itemDescription() {
        return description;
    }




}
