package org.phenopackets.schema.validator.cli;

import org.phenopackets.schema.validator.core.jsonschema.JsonSchemaValidator;
import org.phenopackets.schema.validator.core.validation.ValidationItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class ValidatorApplicationRunner implements CommandLineRunner, ExitCodeGenerator {
    private static Logger LOG = LoggerFactory
            .getLogger(ValidatorApplicationRunner.class);
    private final ValidateCommand myCommand;

    private final IFactory factory; // auto-configured to inject PicocliSpringFactory

    private int exitCode;

    public ValidatorApplicationRunner(ValidateCommand myCommand, IFactory factory) {
        this.myCommand = myCommand;
        this.factory = factory;
    }


    @Override
    public void run(String... args) {
        exitCode = new CommandLine(myCommand, factory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}

