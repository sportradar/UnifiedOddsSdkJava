package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@SuppressWarnings({ "ConstantName", "HideUtilityClassConstructor", "LineLength" })
public class XmlMessageReader {

    private static final Deserializer deserializer;

    static {
        try {
            JAXBContext context = JAXBContext.newInstance(
                "com.sportradar.uf.datamodel:com.sportradar.uf.sportsapi.datamodel:com.sportradar.uf.custombet.datamodel"
            );
            deserializer = new DeserializerImpl(context);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXBContext", e);
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
