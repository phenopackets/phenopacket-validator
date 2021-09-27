package org.phenopackets.validator.cli.results;

import org.phenopackets.validator.core.ValidatorInfo;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidationTsvVisualizer {

    private final List<ValidatorInfo> errorFreeValidations;
    private final Map<ValidatorInfo, ValidationItemTsvVisualizer> errors;


    public ValidationTsvVisualizer() {
        errorFreeValidations = new ArrayList<>();
        errors = new HashMap<>();
    }

    /**
     * Add a ValidationInfo (i.e., validation type) for which no errors were found.
     * @param vinfo validation info for a test that was error free
     */
    public void errorFree(ValidatorInfo vinfo) {
        errorFreeValidations.add(vinfo);
    }

    public void error(ValidatorInfo vinfo, ValidationItemTsvVisualizer result) {
        errors.put(vinfo, result);
    }



    private static String tsvHeader() {
        return String.join("\t", List.of("Error Type", "Message", "Validator Id"));
    }


    public void write(Writer writer) throws IOException {
        writer.write(tsvHeader() + "\n");
        for (var result : this.errors.values()) {
            writer.write(String.join("\t",result.getFields()) + "\n");
        }
        for (var vinfo : this.errorFreeValidations) {
            writer.write(String.join("\t",List.of(vinfo.validatorName().toString(), "no errors", vinfo.validatorId().toString())) + "\n");
        }
    }


}
