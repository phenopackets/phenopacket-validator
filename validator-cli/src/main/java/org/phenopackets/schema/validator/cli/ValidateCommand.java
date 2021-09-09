package org.phenopackets.schema.validator.cli;


import org.phenopackets.schema.validator.core.PhenopacketValidator;
import org.phenopackets.schema.validator.core.jsonschema.JsonSchemaValidator;
import org.phenopackets.schema.validator.core.validation.ValidationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@Component
@Command(name = "validate",
        mixinStandardHelpOptions = true)
public class ValidateCommand implements Callable<Integer> {
    private static Logger LOG = LoggerFactory
            .getLogger(ValidateCommand.class);
    @Option(names = {"-p","--phenopacket"}, description = "Path to phenopacket", required = true)
    private String phenopacketPath;

    @Option(names = "--rare", description = "apply HPO rare-disease constraints")
    private boolean rareHpoConstraints = false;

    @Parameters(description = "one or more phenopacket files")
    private List<String> positionals;




    @Override
    public Integer call() {
        System.out.printf("mycommand was called with --rare=%s and positionals: %s%n", rareHpoConstraints, positionals);
        LOG.info("EXECUTING : command line runner");
        PhenopacketValidator validator;
        if (rareHpoConstraints) {
            validator = new PhenopacketValidator(phenopacketPath, PhenopacketValidator.ValidationType.RARE_DISEASE_VALIDATION);
        } else if (positionals.size() > 0) {
            String[] itemsArray = new String[positionals.size()];
            itemsArray = positionals.toArray(itemsArray);
            validator = new PhenopacketValidator(phenopacketPath, itemsArray);
        } else {
            validator = new PhenopacketValidator(phenopacketPath);
        }
        List<? extends ValidationItem> errors = validator.getValidationErrors();
        if (errors.isEmpty()) {
            System.out.println("\t no errors found");
        } else {
            for (ValidationItem ve : errors) {
                System.out.println("\t(" + ve.errorType() + ") " + ve.message());
            }
        }
        return 0;
    }

}