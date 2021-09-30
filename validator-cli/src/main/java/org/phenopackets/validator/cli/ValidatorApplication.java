package org.phenopackets.validator.cli;

import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.phenopackets.validator.cli.results.ValidationItemTsvVisualizer;
import org.phenopackets.validator.cli.results.ValidationTsvVisualizer;
import org.phenopackets.validator.core.PhenopacketValidator;
import org.phenopackets.validator.core.PhenopacketValidatorRegistry;
import org.phenopackets.validator.core.ValidationItem;
import org.phenopackets.validator.core.ValidatorInfo;
import org.phenopackets.validator.jsonschema.JsonSchemaValidatorFactory;
import org.phenopackets.validator.jsonschema.JsonSchemaValidator;
import org.phenopackets.validator.ontology.HpoValidator;
import org.phenopackets.validator.ontology.OntologyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.*;
import java.nio.file.Files;
import java.util.*;


@CommandLine.Command(name = "PhenopacketValidator", version = "0.1.0", mixinStandardHelpOptions = true)
public class ValidatorApplication implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidatorApplication.class);

    @CommandLine.Option(names = "--rare", description = "apply HPO rare-disease constraints")
    public boolean rareHpoConstraints = false;

    @CommandLine.Parameters(arity = "0..*", description = "one or more JSON Schema configuration files")
    public List<File> jsonSchemaFiles = List.of();

    @CommandLine.Option(names = "--hp", required = true, description = "check with HPO (hp.json)")
    public File hpoJsonPath;

    @CommandLine.Option(names = {"-p", "--phenopacket"}, required = true, description = "Phenopacket file to be validated")
    public String phenopacket;

    @CommandLine.Option(names = {"-o", "--out"}, description = "name of output file (default ${DEFAULT_VALUE})")
    public String outfileName = "phenopacket-validation.tsv";

    private static final String CUSTOM_JSON_VALIDATOR_TYPE = "Custom JSON Schema validation";


    public static void main(String[] args) {
        LOGGER.info("STARTING THE APPLICATION");
        int exitCode = new CommandLine(new ValidatorApplication()).execute(args);
        System.exit(exitCode);
        LOGGER.info("APPLICATION FINISHED");
    }


    @Override
    public void run() {
        List<ValidatorInfo> validationTypes = new LinkedList<>();
        validationTypes.add(ValidatorInfo.generic()); // we run this by default
        if (rareHpoConstraints) {
            LOGGER.info("Validating with HPO rare-disease constraints");
            validationTypes.add(ValidatorInfo.rareDiseaseValidation());
        }
        File phenopacketFile = new File(phenopacket);
        LOGGER.info("Validating {} phenopacket", phenopacketFile);
        Map<ValidatorInfo, PhenopacketValidator> validatorMap = new HashMap<>(JsonSchemaValidatorFactory.genericValidator());
        for (File jsonSchema : jsonSchemaFiles) {
            // we will create ValidatorInfo objects based on the names and paths of the files.
            String baseName = jsonSchema.getName();
            ValidatorInfo vinfo = ValidatorInfo.of(CUSTOM_JSON_VALIDATOR_TYPE, baseName);
            PhenopacketValidator jvalid = JsonSchemaValidator.of(jsonSchema, vinfo);
            validatorMap.put(vinfo, jvalid);
            LOGGER.info("Adding configuration file at `{}`", vinfo);
        }
        Ontology hpoOntology = OntologyLoader.loadOntology(hpoJsonPath);
        OntologyValidator hpoValidator = new HpoValidator(hpoOntology);
        validatorMap.put(hpoValidator.info(), hpoValidator);
        PhenopacketValidatorRegistry registry = PhenopacketValidatorRegistry.of(validatorMap);

        // poor man's formatting
        LOGGER.info("");
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("");
        ValidationTsvVisualizer resultVisualizer = new ValidationTsvVisualizer();
        try (InputStream in = Files.newInputStream(phenopacketFile.toPath())) {
            Set<ValidatorInfo> validatorInfoSet = registry.getValidationTypeSet();
            for (var vinfo : validatorInfoSet) {
                var opt = registry.getValidatorForType(vinfo);
                if (opt.isPresent()) {
                    PhenopacketValidator validator = opt.get();
                    List<ValidationItem> validationItems = validator.validate(phenopacketFile);
                    if (validationItems.isEmpty()) {
                        resultVisualizer.errorFree(vinfo);
                    } else {
                        for (var item : validationItems) {
                            resultVisualizer.error(vinfo, new ValidationItemTsvVisualizer(item));
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("Error opening the phenopacket", e);
        }
        // poor man's formatting
        LOGGER.info("");
        LOGGER.info("--------------------------------------------------------------------------------");
        LOGGER.info("");

        // TSV output
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outfileName))) {
            resultVisualizer.write(bw);
        } catch (IOException e) {
            LOGGER.warn("Error writing the validation results", e);
        }
    }

}
