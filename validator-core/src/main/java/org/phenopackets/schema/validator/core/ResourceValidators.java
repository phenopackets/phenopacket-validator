package org.phenopackets.schema.validator.core;

import org.phenopackets.schema.v1.Phenopacket;
import org.phenopackets.schema.v1.core.OntologyClass;
import org.phenopackets.schema.v1.core.Resource;
import org.phenopackets.schema.validator.core.util.MessageUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Requirements for {@link Resource}:
 *
 * <ul>
 * <li>resource is not empty</li>
 * <li>fields <code>id</code>, <code>name</code>, <code>namespace_prefix</code>, <code>url</code>, <code>version</code>, <code>iri_prefix</code> are not empty</li>
 * </ul>
 * <p>
 * Check on {@link Phenopacket} level:
 * <ul>
 * <li>all {@link Resource}s defined in {@link org.phenopackets.schema.v1.core.MetaData} are used at least by one
 * {@link OntologyClass} instance</li>
 * <li>there is no undefined {@link Resource} present for used {@link OntologyClass} bits</li>
 * </ul>
 * </p>
 *<p>
 *  Additional check available: <br><code>namespace_prefix</code> corresponds with <code>iri_prefix</code>
 *  (e.g. if <code>namespace_prefix=HP</code>, then <code>iri_prefix=http://purl.obolibrary.org/obo/HP_</code>
 *</p>
 *
 */
public class ResourceValidators {

    /**
     * E.g. http://purl.obolibrary.org/obo/GENO_
     */
    static final Pattern IRI_PREFIX = Pattern.compile("http://[\\w+./]+/([a-zA-Z]+)_");



    private ResourceValidators() {
        // private no-op
    }


    public static ValidationCheck<Resource> checkResourceIsNotEmpty() {
        return r -> r.equals(Resource.getDefaultInstance())
                ? ValidationResult.fail("Resource is empty")
                : ValidationResult.pass();
    }


    /**
     * @return {@link Validator} for checking that following elements are not empty:
     * <ul>
     * <li><code>id</code></li>
     * <li><code>name</code></li>
     * <li><code>namespace_prefix</code></li>
     * <li><code>url</code></li>
     * <li><code>version</code></li>
     * <li><code>iri_prefix</code></li>
     * </ul>
     */
    public static Validator<Resource> checkRequiredDataIsNotEmpty() {
        return r -> {
            List<ValidationResult> results = new ArrayList<>(6);
            List<Supplier<String>> els = Arrays.asList(r::getId, r::getName,
                    r::getNamespacePrefix, r::getUrl,
                    r::getVersion, r::getIriPrefix);
            List<String> messages = Arrays.asList("Resource id must not be empty", "Resource name must not be empty",
                    "Resource namespace prefix must not be empty", "Resource url must not be empty",
                    "Resource version must not be empty", "Resource iri prefix must not be empty");

            for (int i = 0; i < els.size(); i++) {
                results.add(els.get(i).get().isEmpty()
                        ? ValidationResult.fail(messages.get(i))
                        : ValidationResult.pass());
            }

            // report only not valid
            return results.stream()
                    .filter(ValidationResult::notValid)
                    .collect(Collectors.toList());
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
                    .map(MessageUtils::getIdPrefix)
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
                String termIdPrefix = MessageUtils.getIdPrefix(term);
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


    public static ValidationCheck<Resource> checkNamespacePrefixForOboResource() {
        return r -> {
            String np = r.getNamespacePrefix();
            String ip = r.getIriPrefix();
            Matcher iriMatcher = IRI_PREFIX.matcher(ip);
            if (iriMatcher.matches()) {
                // This will be 'GENO' for ip = 'http://purl.obolibrary.org/obo/GENO_'
                String iriPrefix = iriMatcher.group(1);
                if (!np.equals(iriPrefix)) {
                    return ValidationResult.fail(String.format("Namespace prefix '%s' does not match '%s' present in IRI prefix '%s'",
                            np, iriPrefix, ip));
                } else {
                    return ValidationResult.pass();
                }
            }

            return checkIriPrefixIsWellFormattedForOboResource().validate(r);
        };
    }

    /**
     * Check that IRI prefix is formatted as required.
     */
    public static ValidationCheck<Resource> checkIriPrefixIsWellFormattedForOboResource() {
        return r -> IRI_PREFIX.matcher(r.getIriPrefix()).matches()
                ? ValidationResult.pass()
                : ValidationResult.fail(String.format("IRI prefix '%s' does not match pattern '%s'", r.getIriPrefix(), IRI_PREFIX.pattern()));
    }


//    public static ValidationCheck<Resource> checkUrlMatchesPurl() {
//        return r -> {
//            String iriPrefix = r.getIriPrefix();
//            String namespacePrefix = r.getNamespacePrefix();
//            String ss = iriPrefix.substring(((iriPrefix.length() - 2) - namespacePrefix.length()), iriPrefix.length() - 2);
//            System.out.println(ss);
//            return null;
//        };
//    }

}