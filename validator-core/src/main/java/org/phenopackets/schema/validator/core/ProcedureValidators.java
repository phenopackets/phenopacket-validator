package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Procedure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class ProcedureValidators {

    private ProcedureValidators() {
        // private no-op
    }

    public static ValidationCheck<Procedure> checkNotEmpty() {
        return p -> Procedure.getDefaultInstance().equals(p)
                ? ValidationResult.fail("Procedure must not be empty")
                : ValidationResult.pass();
    }

    public static Validator<Procedure> checkCodeIsWellFormatted() {
        return p -> {
            List<ValidationResult> results = new ArrayList<>();
            OntologyClass code = p.getCode();

            ValidationResult codeNotEmpty = OntologyClassValidators.checkNotEmpty("Procedure code must not be empty").validate(code);
            if (codeNotEmpty.notValid()) { // nothing more to validate, since code is empty
                results.add(codeNotEmpty);
                return results;
            }

            results.add(OntologyClassValidators.checkIdIsWellFormatted().validate(code));
            results.add(OntologyClassValidators.checkLabelIsWellFormatted().validate(code));

            // return failures only
            return results.stream()
                    .filter(ValidationResult::notValid)
                    .collect(Collectors.toList());
        };
    }

}
