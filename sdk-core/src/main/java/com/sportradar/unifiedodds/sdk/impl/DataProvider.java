/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderStreamException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

/**
 * The generic class used to get various data from the Unified API endpoints
 *
 * @param <TOut> valid Unified API endpoint object generated from API xsd schemas
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ClassTypeParameterName", "LineLength" })
public class DataProvider<TOut> {

    private final String uriFormat;
    private final HttpDataFetcher logHttpDataFetcher;
    private final Deserializer deserializer;
    private final String apiHostAndPort;
    private final boolean useApiSsl;
    private final Locale defaultLocale;

    public DataProvider(
        String uriFormat,
        SdkInternalConfiguration config,
        HttpDataFetcher logHttpDataFetcher,
        Deserializer deserializer
    ) {
        Preconditions.checkNotNull(uriFormat);
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(logHttpDataFetcher);
        Preconditions.checkNotNull(deserializer);

        this.uriFormat = uriFormat;
        this.deserializer = deserializer;
        this.logHttpDataFetcher = logHttpDataFetcher;

        useApiSsl = config.getUseApiSsl();
        apiHostAndPort = config.getApiHostAndPort();
        defaultLocale = config.getDefaultLocale();
    }

    public DataProvider(
        String uriFormat,
        String apiHostAndPort,
        boolean useApiSsl,
        Locale defaultLocale,
        LogHttpDataFetcher logHttpDataFetcher,
        Deserializer deserializer
    ) {
        Preconditions.checkNotNull(uriFormat);
        Preconditions.checkNotNull(apiHostAndPort);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(logHttpDataFetcher);
        Preconditions.checkNotNull(deserializer);

        this.uriFormat = uriFormat;
        this.logHttpDataFetcher = logHttpDataFetcher;
        this.deserializer = deserializer;
        this.apiHostAndPort = apiHostAndPort;
        this.useApiSsl = useApiSsl;
        this.defaultLocale = defaultLocale;
    }

    /**
     * If successful returns the requested API endpoint object
     */
    public TOut getData() throws DataProviderException {
        return getData(defaultLocale);
    }

    /**
     * If successful returns the requested API endpoint object
     *
     * @param args that are used with the supplied URI format
     * @return the requested API endpoint object
     */
    public TOut getData(String... args) throws DataProviderException {
        return getData(defaultLocale, args);
    }

    /**
     * If successful returns the requested API endpoint object
     *
     * @param locale the locale that is used with the supplied URI format
     * @param args   that are used with the supplied URI format
     * @return the requested API endpoint object
     */
    public TOut getData(Locale locale, String... args) throws DataProviderException {
        HttpData fetchedContent = fetchData(null, locale, args);

        return deserializeData(fetchedContent);
    }

    /**
     * If successful returns the requested API endpoint object
     *
     * @param locale the locale that is used with the supplied URI format
     * @param args   that are used with the supplied URI format
     * @return the requested API endpoint object and other related information wrapped in a {@link DataWrapper} instance
     */
    public DataWrapper<TOut> getDataWithAdditionalInfo(Locale locale, String... args)
        throws DataProviderException {
        HttpData fetchedContent = fetchData(null, locale, args);

        TOut deserializeData = deserializeData(fetchedContent);

        return new DataWrapper<>(deserializeData, fetchedContent.getHeaders());
    }

    /**
     * If successful returns the requested API endpoint object
     *
     * @param content the content used to make POST request
     * @return the requested API endpoint object
     */
    public TOut postData(Object content) throws DataProviderException {
        try {
            StringEntity entity = new StringEntity(
                deserializer.serialize(content),
                ContentType.APPLICATION_XML
            );
            HttpData fetchedContent = fetchData(entity, null, null);
            return deserializeData(fetchedContent);
        } catch (DeserializationException e) {
            throw new DataProviderException("Data serialization failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    private TOut deserializeData(HttpData fetchedContent) throws DataProviderException {
        try {
            InputStream inputStream = new ByteArrayInputStream(
                fetchedContent.getResponse().getBytes(StandardCharsets.UTF_8)
            );
            return (TOut) deserializer.deserialize(inputStream);
        } catch (DeserializationException e) {
            throw new DataProviderException("Data deserialization failed", e);
        }
    }

    private HttpData fetchData(HttpEntity content, Locale locale, String[] args)
        throws DataProviderException {
        HttpData fetchedContent;
        try {
            String finalUrl = getFinalUrl(locale, args);
            fetchedContent =
                content == null
                    ? logHttpDataFetcher.get(finalUrl)
                    : logHttpDataFetcher.post(finalUrl, content);
        } catch (CommunicationException e) {
            throw new DataProviderException("The requested data was not accessible on the provided URL", e);
        }

        if (Strings.isNullOrEmpty(fetchedContent.getResponse())) {
            throw new DataProviderException("Response data is null");
        }

        return fetchedContent;
    }

    public String getFinalUrl(Locale locale, String arg) {
        String[] forwardArgs = new String[0];
        if (arg != null && !arg.isEmpty()) {
            forwardArgs = new String[1];
            forwardArgs[0] = arg;
        }
        return getFinalUrl(locale, forwardArgs);
    }

    public String getFinalUrl(Locale locale, String[] args) {
        int fwArgSize = (args != null) ? (args.length + 1) : 1;
        String[] forwardArgs = new String[fwArgSize];

        int destPos = 0;
        if (locale != null) {
            forwardArgs[0] = locale.getLanguage().equals("in") ? "id" : locale.getLanguage();
            destPos++;
        }

        if (args != null) {
            System.arraycopy(args, 0, forwardArgs, destPos, args.length);
        }
        String formattedPath = String.format(uriFormat, (Object[]) forwardArgs);
        String httpHttps = useApiSsl ? "https" : "http";
        return uriFormat.contains("http")
            ? formattedPath
            : httpHttps + "://" + apiHostAndPort + "/v1" + formattedPath;
    }

    @Override
    public String toString() {
        return "DataProvider{" + "uriFormat='" + uriFormat + '\'' + '}';
    }

    /**
     * Method used to wrap up the checked exception in an uncatched exception - streams can not handle checked exception and delegate them forward.
     * For this reason this wrapper method throws a {@link DataProviderStreamException} which can be catched out of the stream.
     *
     * @param provider the provider to perform the fetch with
     * @param l the Locale used for the fetch
     * @param args a list of arguments which should be passed to the provider
     * @param <T> the type of the provider endpoint
     * @return the fetched endpoint object
     */
    public static <T> T streamFetchCatchEndpoint(DataProvider<T> provider, Locale l, String... args) {
        try {
            return provider.getData(l, args);
        } catch (DataProviderException e) {
            throw new DataProviderStreamException(e.getMessage(), e);
        }
    }
}
