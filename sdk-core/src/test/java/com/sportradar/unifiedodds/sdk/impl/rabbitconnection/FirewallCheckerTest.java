/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FirewallCheckerTest {

    private static final String IP_DISCOVERY_URI = "http://ipecho.net/plain";
    private static final int HTTPS_PORT = 443;
    private static final String ANY_URI = "http://any";

    private final LogsMock logsMock = LogsMock.createCapturingFor(FirewallChecker.class);
    private final SocketFactory socketFactory = mock(SocketFactory.class);
    private final BodyOnlyFetchingHttpClient httpClient = mock(BodyOnlyFetchingHttpClient.class);
    private final FirewallChecker firewallChecker = new FirewallChecker(socketFactory, httpClient);

    @BeforeEach
    public void setUp() throws IOException {
        createSocketsOnDemand();
    }

    @Test
    public void shouldNotCreateWithNullSocketFactory() {
        assertThatThrownBy(() -> new FirewallChecker(null, httpClient))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("socketFactory");
    }

    @Test
    public void shouldNotCreateWithNullHttpClient() {
        assertThatThrownBy(() -> new FirewallChecker(socketFactory, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("bodyOnlyFetchingHttpClient");
    }

    @Test
    public void shouldThrowIfProvidedUriIsNotValid() {
        assertThatThrownBy(() -> firewallChecker.checkFirewall("invalidUri{}"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid API host format");
    }

    @Test
    public void shouldUseHttpsPortIfPortIsNotDefined() throws IOException {
        firewallChecker.checkFirewall("http://anyHost");

        verify(socketFactory).openNew(anyString(), eq(HTTPS_PORT));
    }

    @Test
    public void shouldUseSpecifiedPort() throws IOException {
        firewallChecker.checkFirewall("http://anyHost:123");

        final int specifiedPort = 123;
        verify(socketFactory).openNew(anyString(), eq(specifiedPort));
    }

    @Test
    public void shouldUseSpecifiedHostWhenPortIsNotProvided() throws IOException {
        firewallChecker.checkFirewall("http://specifiedHost");

        verify(socketFactory).openNew(eq("specifiedHost"), anyInt());
    }

    @Test
    public void shouldUseSpecifiedHost() throws IOException {
        firewallChecker.checkFirewall("http://specifiedHost:999");

        verify(socketFactory).openNew(eq("specifiedHost"), anyInt());
    }

    @Test
    public void shouldCloseSocketAfterCheckingIt() throws IOException {
        final val socket = mock(Socket.class);
        when(socketFactory.openNew(anyString(), anyInt())).thenReturn(socket);

        firewallChecker.checkFirewall(ANY_URI);

        verify(socket).close();
    }

    @Test
    public void networkDownShouldBeLogged() throws IOException {
        when(socketFactory.openNew(anyString(), anyInt())).thenThrow(UnknownHostException.class);

        assertThatThrownBy(() -> firewallChecker.checkFirewall(ANY_URI));

        logsMock.verifyLoggedLineContaining("Network down");
    }

    @Test
    public void networkDownShouldCauseIoExceptionToBeThrown() throws IOException {
        when(socketFactory.openNew(anyString(), anyInt())).thenThrow(UnknownHostException.class);

        assertThatThrownBy(() -> firewallChecker.checkFirewall(ANY_URI))
            .isInstanceOf(IOException.class)
            .hasMessageContaining("Network down")
            .hasCauseInstanceOf(UnknownHostException.class);
    }

    @Test
    public void nonPermissionRelatedSocketExceptionShouldBeConsideredNonFirewallIssue() throws IOException {
        when(socketFactory.openNew(anyString(), anyInt())).thenThrow(new SocketException("someException"));

        firewallChecker.checkFirewall(ANY_URI);
    }

    @Test
    public void permissionDeniedShouldResultInDiscoveringClientsIpAndIndicateToContactAdministrator()
        throws IOException {
        when(socketFactory.openNew(anyString(), anyInt()))
            .thenThrow(new SocketException("Permission denied"));
        final val clientIp = "122.122.121.120";
        when(httpClient.httpGet(IP_DISCOVERY_URI)).thenReturn(clientIp);

        firewallChecker.checkFirewall(ANY_URI);

        logsMock.verifyLoggedExceptionMessageContaining("please contact Sportradar");
        logsMock.verifyLoggedExceptionMessageContaining(clientIp);
    }

    @Test
    public void ipDiscoveryFailureOnPermissionDeniedShouldEndUpInErrorLogged() throws IOException {
        when(socketFactory.openNew(anyString(), anyInt()))
            .thenThrow(new SocketException("Permission denied"));
        final val ipFetchingError = "ipFetchingError";
        when(httpClient.httpGet(IP_DISCOVERY_URI)).thenThrow(new RuntimeException(ipFetchingError));

        firewallChecker.checkFirewall(ANY_URI);

        logsMock.verifyLoggedExceptionMessageContaining("ipFetchingError");
    }

    private void createSocketsOnDemand() throws IOException {
        final val socket = mock(Socket.class);
        when(socketFactory.openNew(anyString(), anyInt())).thenReturn(socket);
    }
}
