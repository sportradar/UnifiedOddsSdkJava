package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XmlFeedMessageReader {
    private static final Deserializer deserializer;

    static {
        try {
            deserializer = new DeserializerImpl(JAXBContext.newInstance("com.sportradar.uf.datamodel").createUnmarshaller(), null);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXBContext for com.sportradar.uf.datamodel", e);
        }
    }

    public static <T> T readMessageFromResource(String resourceName) throws DeserializationException {
            return readMessageFromStream(Deserializer.class.getClassLoader().getResourceAsStream(resourceName));
    }

    public static <T> T readMessageFromFile(File file) throws IOException, DeserializationException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return readMessageFromStream(stream);
        }
    }

    public static <T> T readMessageFromStream(InputStream stream) throws DeserializationException {
            return (T) deserializer.deserialize(stream);
    }
}
