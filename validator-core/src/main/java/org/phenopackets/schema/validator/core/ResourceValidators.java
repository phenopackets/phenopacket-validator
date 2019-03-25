package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Resource;
import org.phenopackets.schema.validator.core.util.MessageUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResourceValidators {

    private static final Pattern TERM_ID = Pattern.compile("(\\w+):(\\d+)");

    private ResourceValidators() {
        // private no-op
    }

    public static ValidationCheck<Resource> checkResourceIsNotEmpty() {
        return r -> r.equals(Resource.getDefaultInstance())
                ? ValidationResult.fail("Resource is empty")
                : ValidationResult.pass();
    }

    public static ValidationCheck<Resource> checkIriPrefix() {
        return r -> {
            String iriPrefix = r.getIriPrefix();

            return null;
        };
    }

    public static ValidationCheck<Resource> checkUrlSyntax() {
        return r -> {
            String urlString = r.getUrl();
            try {
                URL url = new URL(urlString);
                return ValidationResult.pass();
            } catch (MalformedURLException e) {
                return ValidationResult.fail("Malformed URL - " + e.getMessage());
            }
        };
    }

    /**
     * Check that all {@link Resource}s defined in the {@link org.phenopackets.schema.v1.core.MetaData} are used.
     */
    public static Validator<Phenopacket> checkUnusedResources() {
        return pp -> {
            List<ValidationResult> results = new ArrayList<>();

            List<OntologyClass> ocs = MessageUtils.getEmbeddedMessageFieldsOfType(pp, OntologyClass.class);
            Set<String> termIdPrefixes = ocs.stream()
                    .map(ResourceValidators::getIdPrefix)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());

            for (Resource resource : pp.getMetaData().getResourcesList()) {
                String namespacePrefix = resource.getNamespacePrefix();
                if (!termIdPrefixes.contains(namespacePrefix)) {
                    results.add(ValidationResult.fail(String.format("Unused resource '%s'", resource.getName())));
                }
            }
            return results;
        };
    }

    /**
     * Check that {@link Phenopacket} contains {@link Resource} describing namespace for each present {@link OntologyClass}
     * term.
     */
    public static Validator<Phenopacket> checkUndefinedResorces() {
        return pp -> {
            List<ValidationResult> results = new ArrayList<>();

            Set<String> resourceNamespacePrefixes = pp.getMetaData().getResourcesList().stream()
                    .map(Resource::getNamespacePrefix)
                    .collect(Collectors.toSet());

            List<OntologyClass> terms = MessageUtils.getEmbeddedMessageFieldsOfType(pp, OntologyClass.class);
            for (OntologyClass term : terms) {
                String termIdPrefix = getIdPrefix(term);
                if (termIdPrefix == null || termIdPrefix.isEmpty()) {
                    continue;
                }

                if (!resourceNamespacePrefixes.contains(termIdPrefix)) {
                    results.add(ValidationResult.fail(String.format("Undefined resource for namespace '%s' used in term '%s - %s'",
                            termIdPrefix, term.getId(), term.getLabel())));
                }
            }

            return results;
        };

    }

    public static ValidationCheck<Resource> checkUrlMatchesPurl() {
        return r -> {
            String iriPrefix = r.getIriPrefix();
            String namespacePrefix = r.getNamespacePrefix();
            String ss = iriPrefix.substring(((iriPrefix.length() - 2) - namespacePrefix.length()), iriPrefix.length() - 2);
            System.out.println(ss);
            return null;
        };
    }

    private static String getIdPrefix(OntologyClass oc) {
        Matcher idMatcher = TERM_ID.matcher(oc.getId());
        if (idMatcher.matches()) {
            return idMatcher.group(1);
        }
        return null;
    }

}