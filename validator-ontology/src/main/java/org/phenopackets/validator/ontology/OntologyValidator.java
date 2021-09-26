package org.phenopackets.validator.ontology;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.phenopackets.validator.core.PhenopacketValidator;

import org.phenopackets.validator.core.ValidatorInfo;
import org.monarchinitiative.phenol.io.OntologyLoader;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public abstract class OntologyValidator implements PhenopacketValidator  {
    private static final Logger LOGGER = LoggerFactory.getLogger(OntologyValidator.class);

    protected final ValidatorInfo validatorInfo;
    protected final Ontology ontology;

    protected final static String ONTOLOGY_VALIDATOR = "Otology validation";

    protected OntologyValidator(File jsonFile, ValidatorInfo vinfo) {
        validatorInfo = vinfo;
        ontology = OntologyLoader.loadOntology(jsonFile);
    }

}
