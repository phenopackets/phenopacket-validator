package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.core.Gene;

/**
 * Requirements for {@link Gene}:
 * <ul>
 * <li>gene element must not be empty</li>
 * <li>gene <code>id</code> must be specified</li>
 * <li>gene <code>symbol</code> must be specified</li>
 * </ul>
 */
public class GeneValidators {


    public static ValidationCheck<Gene> checkNotEmpty() {
        return g -> g.equals(Gene.getDefaultInstance())
                ? ValidationResult.fail("Gene must not be empty")
                : ValidationResult.pass();
    }

    public static ValidationCheck<Gene> checkIdIsPresent() {
        return g -> g.getId().isEmpty()
                ? ValidationResult.fail("Gene id must not be empty")
                : ValidationResult.pass();
    }

    public static ValidationCheck<Gene> checkSymbolIsPresent() {
        return g -> g.getSymbol().isEmpty()
                ? ValidationResult.fail("Gene symbol must not be empty")
                : ValidationResult.pass();
    }
}
