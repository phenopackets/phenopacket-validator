package org.phenopackets.schema.validator.core.configval;


import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.phenopackets.schema.validator.core.configval.ConfigAction.SPECIFY_ONTOLOGY;

/**
 * Ingest the configuration with additional constraints for a phenopacket
 * We use the syntax of a Java properties file.
 * @author Peter N Robinson
 */
public class ConfigCommandParser {

    private final static Set<String> ALLOWED_ONTOLOGIES = Set.of("HP", "OMIM", "MONDO", "ORPHA");
    /** Used for error messages. */
    private final static String LIST_OF_ALLOWED_ONTOLOGIES = String.join(", ", ALLOWED_ONTOLOGIES);


    public static List<ConfigCommand> parseCommands(BufferedReader br) throws IOException {
        String line;
        List<ConfigCommand> commands = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("!") || line.startsWith("#")) {
                continue;
            }
            String [] fields = line.split("=");
            String subject = fields[0].trim();
            String action = fields[1].trim();
            if (fields.length != 2) {
                String msg = String.format("[ERROR] Malformed line with %d (instead of the expected 2) fields: %s", fields.length, line);
                throw new PhenopacketValidatorRuntimeException(msg);
            }
            System.out.println(line);
            ConfigSubject configSubject = ConfigSubject.stringToEnum(subject);
            if (action.startsWith("allowedOntologies")) {
                String prefix = getAllowedPrefix(action);
                ConfigCommand configCommand = new ConfigCommand(configSubject, SPECIFY_ONTOLOGY, prefix);
                commands.add(configCommand);
            } else {
                ConfigAction configAction = ConfigAction.stringToEnum(fields[1]);
                ConfigCommand configCommand = new ConfigCommand(configSubject, configAction);
                commands.add(configCommand);
            }
        }
        return commands;
    }



    private static String getAllowedPrefix(String action) {
        int i = action.indexOf("(");
        int j = action.indexOf(")");
        if (i<0 || j<0) {
            throw new PhenopacketValidatorRuntimeException("Malformed allowedOntologies string: " + action);
        }
        String prefix = action.substring(i+1, j);
        if (ALLOWED_ONTOLOGIES.contains(prefix)) {
            return prefix;
        } else {
            String msg = String.format("Did not recognize ontology prefix \"%s\". Current allowed ontologies are %s\n",
                    prefix, LIST_OF_ALLOWED_ONTOLOGIES);
            throw new PhenopacketValidatorRuntimeException(msg);
        }
    }


}
