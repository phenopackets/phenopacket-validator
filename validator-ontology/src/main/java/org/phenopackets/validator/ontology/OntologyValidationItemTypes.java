package org.phenopackets.validator.ontology;

import org.phenopackets.validator.core.ValidationItemType;

class OntologyValidationItemTypes {

    /**
     * This error type refers to the use of a term ID, e.g., HP:1234567, which is syntactically correct but which
     * does not existing in the actual ontology.
     */
    static final ValidationItemType ONTOLOGY_INVALID_ID =
            ValidationItemType.of("Invalid Ontology Term ID", "Term id does not exist in ontology");

    /**
     * This error type refers to the use of a term ID that is an alternate_id, i.e., an outdated ID that should
     * be replaced by the primary ID of the corresponding Ontology term.
     */
    static final ValidationItemType ONTOLOGY_TERM_WITH_ALTERNATE_ID =
            ValidationItemType.of("Use of outdated Ontology Term ID", "Term id is not the primary id for this term");

    static final ValidationItemType TERM_AND_ANCESTOR_ARE_USED =
            ValidationItemType.of("Term and ancestor are used", "Only the child/specific term should be used");

    static final ValidationItemType TERM_IS_USED_AND_ANCESTOR_IS_EXCLUDED =
            ValidationItemType.of("Term is used while ancestor is excluded", "The term is used despite the ancestor term has been excluded");

    private OntologyValidationItemTypes() {}

}
