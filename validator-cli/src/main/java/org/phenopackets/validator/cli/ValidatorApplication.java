package org.phenopackets.validator.cli;

import org.phenopackets.validator.core.PhenopacketValidatorFactory;
import org.phenopackets.validator.jsonschema.ClasspathJsonSchemaValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@SpringBootApplication
public class ValidatorApplication  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorApplication.class);

    public static void main(String[] args) {
        LOGGER.info("STARTING THE APPLICATION");
        SpringApplication.run(ValidatorApplication.class, args);
        LOGGER.info("APPLICATION FINISHED");
    }

    @Bean
    public PhenopacketValidatorFactory phenopacketValidatorFactory() {
        // TODO - for development only...
        return ClasspathJsonSchemaValidatorFactory.defaultValidators();
    }

}
