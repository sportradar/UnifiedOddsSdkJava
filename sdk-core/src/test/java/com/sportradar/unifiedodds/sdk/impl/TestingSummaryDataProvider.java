package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.mockito.Mockito;

@SuppressWarnings({ "IllegalCatch" })
public class TestingSummaryDataProvider<T> extends DataProvider<T> {

    private final Map<String, Supplier<InputStream>> streamFactories;

    public TestingSummaryDataProvider(Map<String, String> filePaths) {
        super(
            "",
            Mockito.mock(SdkInternalConfiguration.class),
            Mockito.mock(LogHttpDataFetcher.class),
            Mockito.mock(Deserializer.class)
        );
        this.streamFactories =
            filePaths
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey(),
                        entry ->
                            () ->
                                TestingSummaryDataProvider.class.getClassLoader()
                                    .getResourceAsStream(entry.getValue())
                    )
                );
    }

    @Override
    public T getData(Locale locale, String... args) throws DataProviderException {
        String eventId = args[0];

        if (eventId.contains("match")) {
            return readFromStream(streamFactories.get("match"));
        } else if (eventId.contains("stage")) {
            return readFromStream(streamFactories.get("stage"));
        } else if (eventId.contains("tournament")) {
            return readFromStream(streamFactories.get("tournament"));
        }

        throw new IllegalArgumentException();
    }

    @Override
    public DataWrapper<T> getDataWithAdditionalInfo(Locale locale, String... args)
        throws DataProviderException {
        throw new UnsupportedOperationException();
    }

    @Override
    public T postData(Object content) {
        throw new UnsupportedOperationException();
    }

    private T readFromStream(Supplier<InputStream> streamFactory) throws DataProviderException {
        try (InputStream stream = streamFactory.get()) {
            return XmlMessageReader.readMessageFromStream(stream);
        } catch (Exception e) {
            throw new DataProviderException("Data serialization failed", e);
        }
    }
}
