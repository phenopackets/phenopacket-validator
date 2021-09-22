package org.phenopackets.validator.cli;


import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;
import org.phenopackets.validator.core.PhenopacketValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "validate",
        mixinStandardHelpOptions = true)
public class ValidateCommand implements Callable<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateCommand.class);

    @Option(names = "--rare", description = "apply HPO rare-disease constraints")
    public boolean rareHpoConstraints = false;

    @Parameters(arity = "0..*", description = "one or more JSON Schema configuration files")
    public List<File> jsonSchemaFiles = List.of();

    @Option(names = {"-p", "--phenopacket"}, required = true, description = "Phenopacket file to be validated")
    public String phenopacket;

    private final PhenopacketValidatorFactory phenopacketValidatorFactory;

    public ValidateCommand(PhenopacketValidatorFactory phenopacketValidatorFactory) {
        this.phenopacketValidatorFactory = phenopacketValidatorFactory;
    }

    @Override
    public Integer call() {
        // What type of validation do we run?
        List<ValidatorInfo> validationTypes = new LinkedList<>();
        validationTypes.add(ValidatorInfo.generic()); // we run this by default
        if (rareHpoConstraints) {
            LOGGER.info("Validating with HPO rare-disease constraints");
            validationTypes.add(ValidatorInfo.rareDiseaseValidation());
        }
        File phenopacketFile = new File(phenopacket);
        LOGGER.info("Validating {} phenopacket", phenopacketFile);
        // TODO  -- adapt PhenopacketValidatorFactory to accept multiple JSON Schema files
        for (File jsonSchema : jsonSchemaFiles) {
            LOGGER.info("Adding configuration file at `{}`", jsonSchema.getAbsolutePath());
        }

        // poor man's formatting
        LOGGER.info("");
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("");
        PhenopacketValidation validator = new PhenopacketValidation(phenopacketValidatorFactory);
        try (InputStream in = Files.newInputStream(phenopacketFile.toPath())) {

            List<ValidationItem> validationItems = validator.validate(in, validationTypes.toArray(ValidatorInfo[]::new));
            if (validationItems.isEmpty()) {
                LOGGER.info("No errors found");
            } else {
                LOGGER.info("Found {} errors:", validationItems.size());
                for (ValidationItem item : validationItems) {
                    LOGGER.info("({}) {}", item.errorType(), item.message());
                }
            }

        } catch (IOException e) {
            LOGGER.warn("Error opening the phenopacket", e);
        }

        // poor man's formatting
        LOGGER.info("");
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("");



        return 0;
    }

}