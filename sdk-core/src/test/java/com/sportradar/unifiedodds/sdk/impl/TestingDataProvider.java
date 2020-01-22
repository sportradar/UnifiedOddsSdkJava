package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.function.Supplier;

public class TestingDataProvider<T> extends DataProvider<T> {
    private final Supplier<InputStream> streamFactory;
    private final Class<T> clazz;

    public TestingDataProvider(File file, Class<T> clazz) {
        this(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Failed to create stream", e);
            }
        }, clazz);
    }

    public TestingDataProvider(String resourceName, Class<T> clazz) {
        this(() -> TestingDataProvider.class.getClassLoader().getResourceAsStream(resourceName), clazz);
    }

    public TestingDataProvider(Supplier<InputStream> streamFactory, Class<T> clazz) {
        super("", Mockito.mock(SDKInternalConfiguration.class), Mockito.mock(LogHttpDataFetcher.class),
                Mockito.mock(Deserializer.class));
        this.streamFactory = streamFactory;
        this.clazz = clazz;
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
            return XmlMessageReader.readMessageFromStream(stream, clazz);
        } catch (Exception e) {
            throw new DataProviderException("Data serialization failed", e);
        }
    }
}
