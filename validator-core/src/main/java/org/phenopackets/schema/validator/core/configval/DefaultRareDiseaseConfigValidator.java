package org.phenopackets.schema.validator.core.configval;

import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.*;
import java.util.List;

public class DefaultRareDiseaseConfigValidator implements ConfigFileValidator {

    private File phenopacketFile;

    public DefaultRareDiseaseConfigValidator(File phenopacketFile) {
        // ingest the rare disease config
        InputStream is = getClass().getClassLoader().getResourceAsStream("config/rare-disease-default.properties");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }catch (IOException e) {
            throw new PhenopacketValidatorRuntimeException("Could not load rare-disease-default.properties");
        }

        this.phenopacketFile = phenopacketFile;
    }


    @Override
    public List<ValidationItem> validate() {
        return null;
    }
}
