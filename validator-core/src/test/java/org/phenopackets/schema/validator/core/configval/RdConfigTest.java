package org.phenopackets.schema.validator.core.configval;

import org.junit.jupiter.api.Test;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RdConfigTest {

    private static File fileFromClasspath(String path) {
        String fname = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
        return new File(fname);
    }

    @Test
    public void a() {
//        File invalidMyopathyPhenopacket = fileFromClasspath("json/bethlehamMyopathyInvalidExample.json");
//        DefaultRareDiseaseConfigValidator validator = new DefaultRareDiseaseConfigValidator(invalidMyopathyPhenopacket);
//        assertNotNull(validator);
    }

    @Test
    public void testLackOfPhenopacketSubject() {
        File simplePhenopacket = fileFromClasspath("json/validSimplePhenopacket.json");
        DefaultRareDiseaseConfigValidator validator = new DefaultRareDiseaseConfigValidator(simplePhenopacket);
        List<ValidationItem> errors = validator.validate();
        for (var r: errors) {
            System.out.println(r);
        }
        assertEquals(1, errors.size());

    }


}
