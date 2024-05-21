/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.collect.Maps;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;
import org.apache.hc.core5.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 10/04/2018.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ConstantName" })
public class DataWrapper<T> {

    private static final Logger logger = LoggerFactory.getLogger(DataWrapper.class);

    private static final String DATE_HEADER_KEY = "Date";
    private static final DateTimeFormatter SERVER_RESPONSE_DATE_FORMAT = DateTimeFormatter.ofPattern(
        "EEE, d MMM yyyy HH:mm:ss z",
        Locale.ENGLISH
    );

    private final T data;
    private final Map<String, String> headers;
    private final ZonedDateTime serverResponseTime;

    public DataWrapper(T data, Header[] headers) {
        this.data = data;

        if (headers != null) {
            this.headers = Maps.newHashMapWithExpectedSize(headers.length);
            for (Header header : headers) {
                this.headers.put(header.getName(), header.getValue());
            }

            this.serverResponseTime = tryParseServerTime(this.headers);
        } else {
            this.headers = null;
            this.serverResponseTime = null;
        }
    }

    public T getData() {
        return data;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ZonedDateTime getServerResponseTime() {
        return serverResponseTime;
    }

    private static ZonedDateTime tryParseServerTime(Map<String, String> headers) {
        if (headers == null || !headers.containsKey(DATE_HEADER_KEY)) {
            logger.warn("Could not find '{}' header in the requested response", DATE_HEADER_KEY);
            return null;
        }

        try {
            return ZonedDateTime.parse(headers.get(DATE_HEADER_KEY), SERVER_RESPONSE_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse '{}' header in the requested response", DATE_HEADER_KEY, e);
            return null;
        }
    }
}
