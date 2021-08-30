package org.phenopackets.schema.validator.core.configval;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        for (var r: validator.validate()) {
            System.out.println(r);
        }
        assertNotNull(validator);

    }


}
