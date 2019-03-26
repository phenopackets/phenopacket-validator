package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Family;
import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.Individual;
import org.phenopackets.schema.v1.core.MetaData;
import org.phenopackets.schema.v1.core.Pedigree;
import org.phenopackets.schema.v1.core.Pedigree.Person;
import org.phenopackets.schema.v1.core.Resource;

import java.util.ArrayList;
import java.util.List;

public class RareDiseaseFamilyValidator implements Validator<Family> {

    /**
     * {@link Family} created to represent <em>proband</em> and his/her <em>relatives</em> in the rare disease context
     * must comply with the following rules:
     * <ul>
     * <li><b>Family</b><ul>
     * <li>must have <code>id</code></li>
     * <li>must have <code>proband</code></li>
     * <li><code>proband</code>, as well as <code>relatives</code> must be valid {@link Phenopacket}s by the
     * {@link RareDiseasePhenopacketValidator}</li>
     * </ul>
     * </li>
     *
     * <li><b>Pedigree</b><ul>
     * <li>must be present</li>
     * <li>all the {@link Individual}s - <code>proband</code>, as well as <code>relatives</code> must be represented in the
     * pedigree. There must be a single {@link Individual} present in the {@link Family} (either as <code>proband</code>,
     * or within <code>relatives</code> where {@link Individual#getId()} corresponds to {@link Person#getIndividualId()}
     * for {@link Person}s present in the {@link Pedigree#getPersonsList()}</li>
     * </ul>
     * </li>
     *
     * <li><b>Metadata</b><ul>
     * <li>{@link MetaData} must not be empty</li>
     * <li>{@link MetaData} must not contain an empty {@link Resource}</li>
     * <li><b>Resources</b>
     * <ul>
     * <li>there are no <em>unused</em> {@link Resource}s in the {@link MetaData}</li>
     * <li>there are no <em>undefined</em> {@link Resource}s in the {@link MetaData}</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     *
     * </ul>
     *
     * @param fml {@link Family} to be validated
     * @return only failures are reported, so the returned list will be empty if <code>fml</code> is valid
     */
    @Override
    public List<ValidationResult> validate(Family fml) { // yeah
        List<ValidationResult> results = new ArrayList<>();

        // TODO - implement

        return results;
    }
}
