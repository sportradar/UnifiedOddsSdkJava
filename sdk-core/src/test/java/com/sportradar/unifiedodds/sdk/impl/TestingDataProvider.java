package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import org.mockito.Mockito;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Supplier;

public class TestingDataProvider<T> extends DataProvider<T> {
    private static Deserializer deserializer;
    private final Supplier<InputStream> streamFactory;

    static {
        try {
            deserializer = new DeserializerImpl(JAXBContext.newInstance("com.sportradar.uf.sportsapi.datamodel").createUnmarshaller(), null);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXBContext for com.sportradar.uf.sportsapi.datamodel", e);
        }
    }

    public TestingDataProvider(File file) {
        this(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to create stream", e);
            }
        });
    }

    public TestingDataProvider(String resourceName) {
        this(() -> TestingDataProvider.class.getClassLoader().getResourceAsStream(resourceName));
    }

    public TestingDataProvider(Supplier<InputStream> streamFactory) {
        super("", Mockito.mock(SDKInternalConfiguration.class), Mockito.mock(LogHttpDataFetcher.class), deserializer);
        this.streamFactory = streamFactory;
    }

    @Override
    public T getData(Locale locale, String... args) throws DataProviderException {
        return readFromStream();
    }

    @Override
    public DataWrapper<T> getDataWithAdditionalInfo(Locale locale, String... args) throws DataProviderException {
        return new DataWrapper<>(readFromStream(), null);
    }

    @Override
    public T postData(Object content) throws DataProviderException {
        return readFromStream();
    }

    private T readFromStream() throws DataProviderException {
        try (InputStream stream = streamFactory.get()) {
            return (T) deserializer.deserialize(stream);
        } catch (Exception e) {
            throw new DataProviderException("Data serialization failed", e);
        }
    }
}
