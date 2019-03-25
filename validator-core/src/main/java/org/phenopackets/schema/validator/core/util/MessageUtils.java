package org.phenopackets.schema.validator.core.util;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MessageUtils {

    /**
     * Get all message fields with <code>type</code> {@link T} that are embedded in <code>message</code>.
     * <p>Does not work for <a href="https://developers.google.com/protocol-buffers/docs/reference/java-generated#singular-fields-proto3">
     * singular fields</a>, or for enums. Does not return default values/instances of {@link T}.
     *
     * @param message message to be searched
     * @param type    embedded fields that we are looking for
     * @param <T>     class of embedded fields that we are looking for
     * @param <U>     class of <code>message</code> that is being searched
     * @return result {@link Set} with embedded fields
     */
    public static <T extends Message, U extends Message> List<T> getEmbeddedMessageFieldsOfType(U message, Class<T> type) {
        ImmutableList.Builder<T> resultBuilder = ImmutableList.builder();
        // Let's say we are looking for type Phenotype. Then, the canonicalName is 'org.phenopackets.schema.v1.core.Phenotype'
        String canonicalName = type.getCanonicalName();

        message.getAllFields().forEach((fieldDescriptor, field) -> {
            // As written in Javadoc, this method doesn't allow to search for "primitives", as int32, string, enums. Only message objects
            if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                // This might be 'org.phenopackets.schema.v1.core.Individual'
                String fullName = fieldDescriptor.getMessageType().getFullName();
                final boolean isRepeated = fieldDescriptor.isRepeated();
                if (fullName.equals(canonicalName)) { // --> type equality check <--
                    if (isRepeated) { // field might be declared as 'repeated' in the proto file
                        @SuppressWarnings("unchecked")
                        Collection<T> repeated = (Collection<T>) field;
                        resultBuilder.addAll(repeated);
                    } else {
                        // we checked for type equality above
                        @SuppressWarnings("unchecked")
                        T instance = (T) field;
                        resultBuilder.add(instance);
                        // same as above
                        @SuppressWarnings("unchecked")
                        U kd = (U) field;
                        resultBuilder.addAll(getEmbeddedMessageFieldsOfType(kd, type));
                    }
                }

                // descend recursively
                if (isRepeated) {
                    @SuppressWarnings("unchecked") // must be message, checked above
                            Collection<Message> repeated = ((Collection<Message>) field);
                    for (Message m : repeated) {
                        resultBuilder.addAll(getEmbeddedMessageFieldsOfType(m, type));
                    }
                } else {
                    Message instance = (Message) field;
                    resultBuilder.addAll(getEmbeddedMessageFieldsOfType(instance, type));
                }
            }
        });

        return resultBuilder.build();
    }
}
