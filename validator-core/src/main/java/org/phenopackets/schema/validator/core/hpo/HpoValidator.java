package org.phenopackets.schema.validator.core.hpo;

import com.google.common.collect.ImmutableList;
import org.monarchinitiative.phenol.formats.hpo.HpoOnset;
import org.monarchinitiative.phenol.formats.hpo.HpoOntology;
import org.phenopackets.schema.v1.PhenoPacket;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.validator.core.ErrorCode;
import org.phenopackets.schema.validator.core.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for validating HPO terms in a {@link org.phenopackets.schema.v1.PhenoPacket}.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class HpoValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(HpoValidator.class);

    private PhenoPacket packet;

    private boolean isValid;

    private final HpoOntology ontology;

    private final List<ErrorCode> errors;


    public HpoValidator(HpoOntology ontology){
        this.ontology=ontology;
        errors=new ArrayList<>();
        isValid=true;//valid until we find the first error
    }


    @Override
    public void validate(PhenoPacket phenoPacket) {
        this.packet=phenoPacket;
        logger.info("validating HPO terms in phenopacket...");
        checkExistenceOfMetadata();
    }

    public boolean isValid() {
        return this.isValid;
    }

    public List<ErrorCode> getErrors() {
        return ImmutableList.copyOf(errors);
    }


    private void checkExistenceOfMetadata() {
        MetaData md=packet.getMetaData();
        if  (! md.isInitialized()) {
//        if (packet.getMetaData()==null ) {
            errors.add(ErrorCode.LACKS_METADATA);
            isValid = false;
       } else {
            System.out.println("MetaData=\""+md.toString()+"\"");
        }
    }





}
