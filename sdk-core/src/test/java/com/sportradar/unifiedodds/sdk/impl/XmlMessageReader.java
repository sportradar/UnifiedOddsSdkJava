package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlMessageReader {
    private static final Map<String, Deserializer> deserializers;

    static {
        try {
            deserializers = new HashMap<>();
            deserializers.put("com.sportradar.uf.datamodel",
                    new DeserializerImpl(JAXBContext.newInstance("com.sportradar.uf.datamodel").createUnmarshaller(), null));
            deserializers.put("com.sportradar.uf.sportsapi.datamodel",
                    new DeserializerImpl(JAXBContext.newInstance("com.sportradar.uf.sportsapi.datamodel").createUnmarshaller(), null));
            deserializers.put("com.sportradar.uf.custombet.datamodel",
                    new DeserializerImpl(JAXBContext.newInstance("com.sportradar.uf.custombet.datamodel").createUnmarshaller(), null));
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXBContext", e);
        }
    }

    public static <T> T readMessageFromResource(String resourceName, Class<T> clazz) throws DeserializationException {
        return readMessageFromStream(Deserializer.class.getClassLoader().getResourceAsStream(resourceName), clazz);
    }

    public static <T> T readMessageFromFile(File file, Class<T> clazz) throws IOException, DeserializationException {
        try (FileInputStream stream = new FileInputStream(file)) {
            return readMessageFromStream(stream, clazz);
        }
    }

    public static <T> T readMessageFromStream(InputStream stream, Class<T> clazz) throws DeserializationException {
        Deserializer deserializer = deserializers.get(clazz.getPackage().getName());
        return (T) deserializer.deserialize(stream);
    }
}
