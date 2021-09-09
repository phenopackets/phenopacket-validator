package org.phenopackets.schema.validator.cli;

import org.phenopackets.schema.validator.core.jsonschema.JsonSchemaValidator;
import org.phenopackets.schema.validator.core.validation.ValidationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;


/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@SpringBootApplication
public class ValidatorApplication  {

    private static Logger LOG = LoggerFactory
            .getLogger(ValidatorApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING THE APPLICATION");
        SpringApplication.run(ValidatorApplication.class, args);
        LOG.info("APPLICATION FINISHED");
    }


}
