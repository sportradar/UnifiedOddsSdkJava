/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.commoniam;

import com.google.common.base.Splitter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;

public class UrlEncodedParams {

    public static List<String> extractsJwtFrom(List<String> requestBodies)
        throws UnsupportedEncodingException {
        return requestBodies
            .stream()
            .map(requestBody -> {
                try {
                    return extractJwtFrom(requestBody);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            })
            .collect(java.util.stream.Collectors.toList());
    }

    public static String extractJwtFrom(String requestBody) throws UnsupportedEncodingException {
        Map<String, String> parameters = parseFormData(requestBody);

        Assertions.assertThat(parameters).containsKey("client_assertion");
        return URLDecoder.decode(parameters.get("client_assertion"), "UTF-8");
    }

    public static Map<String, String> parseFormData(String requestBody) {
        return Splitter.on('&').withKeyValueSeparator('=').split(requestBody);
    }
}
