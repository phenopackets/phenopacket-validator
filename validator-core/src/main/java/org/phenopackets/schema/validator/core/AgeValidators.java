package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.core.Age;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgeValidators {

    /**
     * Date designators are represented by capital [YMWD] characters, each of the designator is optional.
     */
    private static final Pattern DATE_PATTERN = Pattern.compile("P(\\d+Y)?(\\d+M)?(\\d+W)?(\\d+D)?");

    /**
     * Time designators are represented by capital [HMS] characters, each of the designator is optional.
     */
    private static final Pattern TIME_PATTERN = Pattern.compile(".*T(\\d+H)?(\\d+M)?(\\d+S)?");

    /**
     * The string begins with 'P'
     */
    private static final Pattern GENEREAL_PATTERN = Pattern.compile("P.*");

    private AgeValidators() {
        // private no-op
    }

    public static ValidationCheck<Age> ageStringIsWellFormatted() {
        return age -> {

            String ageString = age.getAge();

            // first check initial sanity
            Matcher generalPattern = GENEREAL_PATTERN.matcher(ageString);
            if (!generalPattern.matches()) {
                return ValidationResult.fail(String.format("Missing period designator ('P') in the age '%s'", ageString));
            }

            // 'T' (time designator) is mandatory for the time components of the representation
            final int dIdx = ageString.indexOf('P');
            final int tIdx = ageString.indexOf('T');


            final boolean hasTimePart = tIdx >= 0;

            // process date part
            String dateString = hasTimePart
                    ? ageString.substring(dIdx, tIdx)
                    : ageString;

            String timeString = hasTimePart ? ageString.substring(tIdx) : "";

            Matcher dateMatcher = DATE_PATTERN.matcher(dateString);
            if (dateMatcher.matches()) {
                // date can be empty ('P'), that is OK if time is specified
                if (dateString.equals("P")) {
                    if (hasTimePart) { // this covers 'PT'
                        // if period part is empty, then time part must not be empty
                        String timePart = ageString.substring(tIdx);
                        if (timePart.equals("T")) {
                            // empty date, empty time, nothing to do more
                            return ValidationResult.fail("You have to use at least one date|time element");
                        } else {
                            // process time part
                            Matcher timeMatcher = TIME_PATTERN.matcher(timePart);
                            if (timeMatcher.matches()) {
                                // time is OK, so no complaints
                            } else {
                                // checked date, checked time, nothing to do more
                                return ValidationResult.fail(String.format("Time part of '%s' is invalid", ageString));
                            }
                        }
                    } else { // this covers 'P'
                        // empty date, no time, nothing to do more
                        return ValidationResult.fail("You have to use at least one date|time element");
                    }
                }
            } else {
                // date part is invalid
                return ValidationResult.fail(String.format("Date part of '%s' is invalid", ageString));
            }

            if (hasTimePart) {
                Matcher timeMatcher = TIME_PATTERN.matcher(timeString);
                if (!timeMatcher.matches()) {
                    return ValidationResult.fail(String.format("Time part of '%s' is invalid", ageString));
                }

            }
            return ValidationResult.pass();
        };
    }
}
