package org.phenopackets.schema.validator.cli;

import org.phenopackets.schema.validator.core.jsonschema.JsonSchemaValidator;
import org.phenopackets.schema.validator.core.jsonschema.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.util.List;


/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@SpringBootApplication
public class ValidatorApplication implements CommandLineRunner {

    private static Logger LOG = LoggerFactory
            .getLogger(ValidatorApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(ValidatorApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        LOG.info("EXECUTING : command line runner");
        if (args.length < 1) {
            System.err.println("[USAGE] java -jar xx.jar phenopacket1 [phenopacket2 ....]");
            return;
        }
        JsonSchemaValidator validator = new JsonSchemaValidator();
        for (String phenopacket: args) {
            File f = new File(phenopacket);
            if (! f.isFile()) {
                System.err.println("[ERROR] Could not find file " + phenopacket);
                continue;
            }
            System.out.printf("Validating %s\n", f.getAbsoluteFile());
            List<ValidationError> errors = validator.validate(phenopacket);
            if (errors.isEmpty()) {
                System.out.println("\t no errors found");
            } else {
                for (ValidationError ve : errors) {
                    System.out.println("\t" + ve.getMessage());
                }
            }
        }
        for (int i = 0; i < args.length; ++i) {
            LOG.info("args[{}]: {}", i, args[i]);
        }
    }
}
