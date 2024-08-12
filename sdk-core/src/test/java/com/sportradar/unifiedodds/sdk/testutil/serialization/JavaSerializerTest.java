/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.junit.jupiter.api.Test;

public class JavaSerializerTest {

    private static final byte[] RANDOM_BYTES = { 4, 7, 5, 1, 2, 4, 5, 6 };

    @Test
    public void notSerializesObjectWhichAreNotMarkedAsSerializable() throws Exception {
        assertThatThrownBy(() -> JavaSerializer.serialize(new NotMarkedWithSerializable()))
            .isInstanceOf(NotSerializableException.class);
    }

    @Test
    public void doesNotDeserializeBytesWhichDoesNotRepresentObject() throws Exception {
        assertThatThrownBy(() -> JavaSerializer.deserialize(RANDOM_BYTES))
            .isInstanceOf(StreamCorruptedException.class);
    }

    @Test
    public void serializationProcessShouldPreserveFieldValuesSetInitially() throws Exception {
        String value = "someValue";
        val originalObject = new SerializableClass();
        originalObject.setSomeField(value);

        val deserialized = (SerializableClass) JavaSerializer.deserialize(
            JavaSerializer.serialize(originalObject)
        );

        assertThat(deserialized.getSomeField()).isEqualTo(value);
    }

    @Test
    public void deserializationCreatesAnotherInstanceOfTheObject() throws Exception {
        String value = "someValue";
        val originalObject = new SerializableClass();
        originalObject.setSomeField(value);

        val deserialized = (SerializableClass) JavaSerializer.deserialize(
            JavaSerializer.serialize(originalObject)
        );

        assertThat(deserialized).isNotSameAs(originalObject);
    }

    public static class NotMarkedWithSerializable {

        private int anyField;
    }

    @Setter
    @Getter
    public static class SerializableClass implements Serializable {

        private String someField;
    }
}
