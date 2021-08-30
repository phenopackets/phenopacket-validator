package org.phenopackets.schema.validator.core.configval;

public class ConfigCommand {


    public ConfigSubject getSubject() {
        return subject;
    }

    public ConfigAction getAction() {
        return action;
    }

    public String getPayload() {
        return payload;
    }



    private final ConfigSubject subject;

    private final ConfigAction action;
    /** Some Task/Action combinations have a target Ontology term prefix that we represent with a String. Can be null*/
    private final String payload;


    public ConfigCommand(ConfigSubject csubj, ConfigAction action, String payload) {
        this.subject = csubj;
        this.action = action;
        this.payload = payload;
    }

    public ConfigCommand(ConfigSubject csubj, ConfigAction action) {
        this.subject = csubj;
        this.action = action;
        this.payload = null;
    }





}
