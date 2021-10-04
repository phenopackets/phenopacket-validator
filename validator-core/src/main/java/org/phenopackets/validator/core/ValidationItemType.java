package org.phenopackets.validator.core;

public interface ValidationItemType {

    static ValidationItemType of(String itemType, String itemDescription) {
        return new ValidationItemTypeDefault(itemType, itemDescription);
    }

    String itemType();

    String itemDescription();

}
