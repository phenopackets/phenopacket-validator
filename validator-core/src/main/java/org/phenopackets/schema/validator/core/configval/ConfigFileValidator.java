package org.phenopackets.schema.validator.core.configval;


import com.google.protobuf.util.JsonFormat;
import org.phenopackets.schema.v2.Phenopacket;
import org.phenopackets.schema.validator.core.except.PhenopacketValidatorRuntimeException;
import org.phenopackets.schema.validator.core.validation.ValidationItem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public interface ConfigFileValidator {
    public List<ValidationItem> validate();

    default Phenopacket fromFile(File f) {
        try (FileReader reader = new FileReader(f)) {
            Phenopacket.Builder phenoPacketBuilder = Phenopacket.newBuilder();
            JsonFormat.parser().merge(reader, phenoPacketBuilder);
            return phenoPacketBuilder.build();
        }catch (IOException e) {
            throw new PhenopacketValidatorRuntimeException("Could not open Phenopacket file at " + f.getAbsolutePath());
        }
    }

    default Phenopacket fromJson(String jsonString) throws IOException {
        Phenopacket.Builder phenoPacketBuilder = Phenopacket.newBuilder();
        JsonFormat.parser().merge(jsonString, phenoPacketBuilder);
        return phenoPacketBuilder.build();
    }
}
