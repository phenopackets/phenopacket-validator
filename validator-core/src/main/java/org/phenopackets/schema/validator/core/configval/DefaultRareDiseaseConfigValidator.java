package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultRareDiseaseConfigValidator implements ConfigFileValidator {

    private final List<ConfigCommand> commands;

    public List<ValidationItem> getErrors() {
        return errors;
    }

    private final Phenopacket phenopacket;

    private final List<ValidationItem> errors;
    public DefaultRareDiseaseConfigValidator(File phenopacketFile) {
        // ingest the rare disease config
        InputStream is = getClass().getClassLoader().getResourceAsStream("config/rare-disease-default.properties");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            this.commands = ConfigCommandParser.parseCommands(br);
        }catch (IOException e) {
            throw new PhenopacketValidatorRuntimeException("Could not load rare-disease-default.properties");
        }
        phenopacket = fromFile(phenopacketFile);
        errors = new ArrayList<>();
    }


    @Override
    public List<ValidationItem> validate() {
        ConfigCommandValidator configCommandValidator = new ConfigCommandValidator(phenopacket);
        return configCommandValidator.getErrors();
    }


}
