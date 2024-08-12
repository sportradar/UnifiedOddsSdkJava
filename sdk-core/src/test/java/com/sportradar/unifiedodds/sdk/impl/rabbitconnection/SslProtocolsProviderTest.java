/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SslProtocolsProviderTest {

    private final SslProtocolsProvider sslProtocolsProvider = new SslProtocolsProvider();

    @Test
    public void shouldProvideNoneIfNoneIsSupported() throws NoSuchAlgorithmException {
        String[] originalOrder = new String[0];

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Collections.emptyList();
        assertEquals(expectedOrder, providedOrder);
    }

    @Test
    public void shouldProvideOnlyOneIfOnlyOneIsSupported() throws NoSuchAlgorithmException {
        final val protocol = "supportedProtocol";
        String[] originalOrder = new String[] { protocol };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(protocol);
        assertEquals(expectedOrder, providedOrder);
    }

    @Test
    public void shouldProvideNoneIfTheOnlyProtocolAvailableIsNull() throws NoSuchAlgorithmException {
        String[] originalOrder = new String[] { null };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Collections.emptyList();
        assertEquals(expectedOrder, providedOrder);
    }

    @Test
    public void shouldProvideOnlyOneTlsIfOnlyOneIsSupported() throws NoSuchAlgorithmException {
        final val protocol = "TLSv1";
        String[] originalOrder = new String[] { protocol };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(protocol);
        assertEquals(expectedOrder, providedOrder);
    }

    @Test
    public void shouldReverseOrderIfNoneIsTlsToPreserveLegacyBehaviour() throws NoSuchAlgorithmException {
        final val p1 = "protocolB";
        final val p2 = "protocolA";
        final val p3 = "protocolC";
        String[] originalOrder = new String[] { p1, p2, p3 };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(p3, p2, p1);
        assertEquals(expectedOrder, providedOrder);
    }

    @ParameterizedTest
    @CsvSource(
        { "TLSv1.1, protocolA, protocolC", "protocolB, TLSv1.1, protocolC", "protocolB, protocolA, TLSv1.1" }
    )
    public void shouldReverseOrderIfOnlyOneTlsVersionIsInOriginalListToPreserveLegacyBehaviour(
        final String p1,
        final String p2,
        final String p3
    ) throws NoSuchAlgorithmException {
        String[] originalOrder = new String[] { p1, p2, p3 };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(p3, p2, p1);
        assertEquals(expectedOrder, providedOrder);
    }

    @ParameterizedTest
    @CsvSource({ "TLSv1, TLSv1.1", "TLSv1.1, TLSv1.2", "TLSv1, TLSv1.2" })
    public void shouldProvideNewerTlsFirstEvenIfOriginalOrderIsOpposite(final String p1, final String p2)
        throws NoSuchAlgorithmException {
        String[] originalOrder = new String[] { p1, p2 };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(p2, p1);
        assertEquals(expectedOrder, providedOrder);
    }

    @ParameterizedTest
    @CsvSource({ "TLSv1.1, TLSv1", "TLSv1.2, TLSv1.1", "TLSv1.2, TLSv1" })
    public void shouldProvideNewerTlsFirstWhenOriginalOrderDoesNotNeedToBeModified(
        final String p1,
        final String p2
    ) throws NoSuchAlgorithmException {
        String[] originalOrder = new String[] { p1, p2 };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(p1, p2);
        assertEquals(expectedOrder, providedOrder);
    }

    @ParameterizedTest
    @CsvSource({ "TLSv1.1, TLSv1, protocolA", "TLSv1.2, protocolB, TLSv1.1", "protocolC, TLSv1.2, TLSv1" })
    public void shouldProvideNewerTlsFirstInNonOnlyTlsListWhenOriginalOrderDoesNotNeedToBeModified(
        final String p1,
        final String p2,
        final String p3
    ) throws NoSuchAlgorithmException {
        String[] originalOrder = new String[] { p1, p2, p3 };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(p1, p2, p3);
        assertEquals(expectedOrder, providedOrder);
    }

    @ParameterizedTest
    @CsvSource({ "TLSv1, TLSv1.1, protocolA", "TLSv1.1, protocolB, TLSv1.2", "protocolC, TLSv1, TLSv1.2" })
    public void shouldProvideNewerTlsFirstInNonOnlyTlsListWhenOriginalOrderIsOpposite(
        final String p1,
        final String p2,
        final String p3
    ) throws NoSuchAlgorithmException {
        String[] originalOrder = new String[] { p1, p2, p3 };

        final List<String> providedOrder = providePrioritisedProtocols(originalOrder);

        List<Object> expectedOrder = Arrays.asList(p3, p2, p1);
        assertEquals(expectedOrder, providedOrder);
    }

    private List<String> providePrioritisedProtocols(String[] toReturn) throws NoSuchAlgorithmException {
        final List<String> protocols;
        try (final val sslContextStatic = mockStatic(SSLContext.class)) {
            final val sslContext = mock(SSLContext.class);
            final val sslParameters = mock(SSLParameters.class);

            when(sslParameters.getProtocols()).thenReturn(toReturn);
            when(sslContext.getSupportedSSLParameters()).thenReturn(sslParameters);
            sslContextStatic.when(SSLContext::getDefault).thenReturn(sslContext);

            protocols = sslProtocolsProvider.provideSupportedPrioritised();
        }
        return protocols;
    }
}
