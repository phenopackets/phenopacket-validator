package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultRareDiseaseConfigValidator implements ConfigFileValidator {

    private final List<ConfigCommand> commands;



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

        for (ConfigCommand configCommand : commands) {
            switch(configCommand.getSubject()) {
                case PHENOPACKET_SUBJECT:
                    constrainPhenopacketSubject(configCommand);
            }
        }

        return List.of();
    }

    private void constrainPhenopacketSubject(ConfigCommand configCommand) {
        if (! phenopacket.hasSubject()) {
           errors.add(ConfigValidationError.phenopacketLacksSubject());

        }
    }
}
