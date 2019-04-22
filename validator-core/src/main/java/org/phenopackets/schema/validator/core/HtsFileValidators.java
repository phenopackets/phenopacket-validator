package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.core.File;
import org.phenopackets.schema.v1.core.HtsFile;

/**
 * Requirements for {@link HtsFile}:
 * <ul>
 * <li><code>hts_format</code> must be specified</li>
 * <li><code>genome_assembly</code> must not be empty</li>
 * <li><code>file</code> must not be empty and at least one of <code>uri</code> and <code>path</code> must be specified</li>
 * <li><code>individual_to_sample_identifiers</code> should not be empty</li>
 * </ul>
 */
public class HtsFileValidators {

//
//     public ValidationCheck<HtsFile> htsFormatIsPresent() {
//         return hf -> {
//             hf.getHtsFormat().equals(HtsFile.HtsFormat.UNKNOWN)
//         }
//     }

    public static ValidationCheck<HtsFile> genomeAssemblyIsPresent() {
        return hf -> (hf.getGenomeAssembly().isEmpty())
                ? ValidationResult.fail("Genome assembly must not be empty")
                : ValidationResult.pass();
    }


    public static ValidationCheck<HtsFile> fileIsPresentAndValid() {
        return hf -> {
            if (File.getDefaultInstance().equals(hf.getFile())) {
                return ValidationResult.fail("File must not be empty");
            }

            return fileIsValid().validate(hf.getFile());
        };
    }


    public static ValidationCheck<File> fileIsValid() {
        return f -> !f.getPath().isEmpty() || !f.getUri().isEmpty()
                ? ValidationResult.pass()
                : ValidationResult.fail("At least one of 'path' and 'uri' must be specified");
    }


    public static ValidationCheck<HtsFile> individualToSampleIdentifiersArePresent() {
        return hf -> hf.getIndividualToSampleIdentifiersCount() > 0
                ? ValidationResult.pass()
                : ValidationResult.warn("Map of individual to sample ids is empty");
    }
}
