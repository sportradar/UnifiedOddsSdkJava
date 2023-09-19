/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;

public class SslProtocolsProvider {

    List<String> provideSupportedPrioritised() throws NoSuchAlgorithmException {
        final List<String> sslProtocols = extractSslProtocolsFromContext();
        if (containAtLeast2TlsOccurences(sslProtocols)) {
            prioritiseNewerTlsFirst(sslProtocols);
        } else {
            Collections.reverse(sslProtocols);
        }
        return sslProtocols;
    }

    private static List<String> extractSslProtocolsFromContext() throws NoSuchAlgorithmException {
        return Arrays
            .asList(SSLContext.getDefault().getSupportedSSLParameters().getProtocols())
            .stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private static void prioritiseNewerTlsFirst(List<String> sslProtocols) {
        final List<String> twoTlsProtocols = getFirst2TlsOccurrences(sslProtocols);
        final int indexOfFirstTlsOccurrence = sslProtocols.indexOf(twoTlsProtocols.get(0));
        final int indexOfSecondTlsOccurrence = sslProtocols.indexOf(twoTlsProtocols.get(1));
        final String firstTlsOccurrenceVersion = sslProtocols.get(indexOfFirstTlsOccurrence);
        final String secondTlsOccurrenceVersion = sslProtocols.get(indexOfSecondTlsOccurrence);
        if (firstTlsOccurrenceVersion.compareTo(secondTlsOccurrenceVersion) < 0) {
            Collections.reverse(sslProtocols);
        }
    }

    private static boolean containAtLeast2TlsOccurences(List<String> sslProtocols) {
        final List<String> twoTlsProtocols2 = getFirst2TlsOccurrences(sslProtocols);
        return twoTlsProtocols2.size() == 2;
    }

    private static List<String> getFirst2TlsOccurrences(List<String> sslProtocols) {
        return sslProtocols.stream().filter(p -> p.startsWith("TLS")).limit(2).collect(Collectors.toList());
    }
}
