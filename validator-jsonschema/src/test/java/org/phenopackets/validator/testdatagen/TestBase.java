package org.phenopackets.validator.testdatagen;


import org.phenopackets.schema.v2.core.OntologyClass;


import static org.phenopackets.phenotools.builder.builders.OntologyClassBuilder.ontologyClass;


public class TestBase {
    public static final String SCHEMA_VERSION = "2.0";
    public static final OntologyClass CONGENITAL_ONSET = ontologyClass("HP:0003577", "Congenital onset");
    public static final OntologyClass CHILDHOOD_ONSET = ontologyClass("HP:0011463", "Childhood onset");
    public static final OntologyClass ADULT_ONSET = ontologyClass("HP:0003581", "Adult onset");
    public static final OntologyClass SEVERE = ontologyClass("HP:0012828", "Severe");





}
