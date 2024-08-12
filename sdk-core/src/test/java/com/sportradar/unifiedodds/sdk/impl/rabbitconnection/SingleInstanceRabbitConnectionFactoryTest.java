/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.sportradar.utils.time.TimeInterval.minutes;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.SdkConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.utils.time.EpochMillis;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class SingleInstanceRabbitConnectionFactoryTest {

    private static final String CONNECTION_WAS_NULL = "connection was null";
    private static final String CONNECTION_SHOULD_BE_ABLE_TO_OPEN = "connection should be able to open";
    private static final EpochMillis NOON = new EpochMillis(1664445600000L);
    private static final long NO_CONNECTION_TIMESTAMP = 0L;
    private static final String ANY = "any";
    private static final String CONNECTION_HAS_BEEN_SHUT_DOWN = "connection has been shut-down";
    private static final String INITIATED_BY_APPLICATION = "initiated by application";
    private ConnectionFixture openConnection = new ConnectionFixture.Holder().get();
    private SdkInternalConfiguration configWithToken = createConfigWithToken();
    private FirewallChecker firewallChecker = mock(FirewallChecker.class);
    private TimeUtils timeUtils = mock(TimeUtils.class);
    private ConnectionFactory rabbitConnectionFactory = mock(ConnectionFactory.class);

    private LogsMock logsMock = LogsMock.createCapturingFor(SingleInstanceAmqpConnectionFactory.class);
    private ExecutorService executorService = mock(ExecutorService.class);

    private SslProtocolsProvider sslProtocolsProvider = mock(SslProtocolsProvider.class);
    private SdkConnectionStatusListener connectionStatusListener = mock(SdkConnectionStatusListener.class);
    private ConfiguredConnectionFactory configuredConnectionFactory = new ConfiguredConnectionFactory(
        rabbitConnectionFactory,
        configWithToken,
        "any",
        connectionStatusListener,
        executorService,
        timeUtils
    );
    private AmqpConnectionFactory factory = new SingleInstanceAmqpConnectionFactory(
        configuredConnectionFactory,
        configWithToken,
        connectionStatusListener,
        mock(WhoAmIReader.class),
        firewallChecker,
        timeUtils,
        sslProtocolsProvider
    );

    public SingleInstanceRabbitConnectionFactoryTest() throws IOException {}

    @Test
    public void shouldNotInstantiateWithoutFirewallChecker() {
        assertThatThrownBy(() ->
                new SingleInstanceAmqpConnectionFactory(
                    configuredConnectionFactory,
                    configWithToken,
                    connectionStatusListener,
                    mock(WhoAmIReader.class),
                    null,
                    timeUtils,
                    sslProtocolsProvider
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("firewallChecker");
    }

    @Test
    public void shouldNotInstantiateWithoutSslProtocolsProvider() {
        assertThatThrownBy(() ->
                new SingleInstanceAmqpConnectionFactory(
                    configuredConnectionFactory,
                    configWithToken,
                    connectionStatusListener,
                    mock(WhoAmIReader.class),
                    firewallChecker,
                    timeUtils,
                    null
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("sslProtocolsProvider");
    }

    @Test
    public void shouldNotInstantiateWithoutTimeUtils() throws NoSuchAlgorithmException {
        assertThatThrownBy(() ->
                new SingleInstanceAmqpConnectionFactory(
                    configuredConnectionFactory,
                    configWithToken,
                    connectionStatusListener,
                    mock(WhoAmIReader.class),
                    firewallChecker,
                    null,
                    sslProtocolsProvider
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("timeUtils");
    }

    @Test
    public void initiallyNoConnectionShouldBeCreated() {
        assertFalse(factory.isConnectionHealthy());
        assertEquals(0, factory.getConnectionStarted());
        assertTrue(factory.canConnectionOpen());
    }

    @Test
    public void closingFactoryBeforeFirstConnectionIsCreatedAndIndicatingFeedIsStillOpenShouldHaveNoEffect()
        throws IOException {
        factory.close(false);

        assertFalse(factory.isConnectionHealthy());
        assertEquals(0, factory.getConnectionStarted());
        assertTrue(factory.canConnectionOpen());
    }

    @Test
    public void shouldBeClosableAlongWithFeedClosureEvenBeforeBeforeFirstConnectionIsCreated()
        throws IOException {
        factory.close(true);

        assertFalse(factory.isConnectionHealthy());
        assertEquals(0, factory.getConnectionStarted());
        assertFalse(factory.canConnectionOpen());
    }

    @Test
    public void creatingConnectionShouldCheckFirewall()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        when(configWithToken.getApiHost()).thenReturn("https://sportradar.com");

        factory.getConnection();

        verify(firewallChecker).checkFirewall("https://sportradar.com");
    }

    @Test
    public void shouldCreateConnectionOnInitialRetrieval() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        Connection connection = at(NOON.get(), () -> factory.getConnection());

        assertNotNull(CONNECTION_WAS_NULL, connection);
        assertTrue("connection should be open", factory.isConnectionHealthy());
        assertEquals(NOON.get(), factory.getConnectionStarted());
        assertTrue(CONNECTION_SHOULD_BE_ABLE_TO_OPEN, factory.canConnectionOpen());
    }

    @Test
    public void shouldNotCreateConnectionIfConnectionHasAlreadyBeenCreated() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        Connection connection = at(NOON.plus(minutes(1)).get(), () -> factory.getConnection());

        assertNotNull(CONNECTION_WAS_NULL, connection);
        assertEquals(NOON.get(), factory.getConnectionStarted());
        verify(rabbitConnectionFactory, times(1)).newConnection(any(ExecutorService.class));
    }

    @Test
    public void connectionShouldBeClosedAfterClosingTheFactoryIndicatingFeedIsStillOpen() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(false);

        assertFalse("connection should be closed", factory.isConnectionHealthy());
        assertEquals(NO_CONNECTION_TIMESTAMP, factory.getConnectionStarted());
        assertTrue(CONNECTION_SHOULD_BE_ABLE_TO_OPEN, factory.canConnectionOpen());
    }

    @Test
    public void connectionShouldBeClosedAfterClosingTheFactoryIndicatingFeedIsClosedAsWell()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(true);

        assertFalse("connection should be closed", factory.isConnectionHealthy());
        assertEquals(NO_CONNECTION_TIMESTAMP, factory.getConnectionStarted());
        assertFalse("connection should not be able to open", factory.canConnectionOpen());
    }

    @Test
    public void failureToCloseConnectionDueToIoExceptionShouldBeLogged() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);
        final val reason = "connectionInUse";
        doThrow(new IOException(reason)).when(openConnection).close();

        at(NOON.get(), () -> factory.getConnection());
        factory.close(true);

        logsMock.verifyLoggedLineContaining("Error closing connection");
        logsMock.verifyLoggedExceptionMessageContaining(reason);
    }

    @Test
    public void shouldCreateConnectionAfterClosingFactory() throws Exception {
        final val connection1 = new ConnectionFixture.Holder().get();
        final val connection2 = new ConnectionFixture.Holder().get();
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class)))
            .thenReturn(connection1, connection2);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(false);
        Connection connection = at(NOON.plus(minutes(1)).get(), () -> factory.getConnection());

        assertNotNull(CONNECTION_WAS_NULL, connection);
        assertTrue("connection should be open", factory.isConnectionHealthy());
        assertEquals(NOON.plus(minutes(1)).get(), factory.getConnectionStarted());
        assertTrue(CONNECTION_SHOULD_BE_ABLE_TO_OPEN, factory.canConnectionOpen());
    }

    @Test
    public void connectionShouldNotBeOpenIfItWasShutDownByRabbitMqUnderTheHoods() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        openConnection.getControlApi().closeInitiatedByRabbitMq();

        assertFalse("connection was open", factory.isConnectionHealthy());
    }

    @Test
    public void shouldNotCreateConnectionAfterClosingFactoryAlongWithClosingTheFeed() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(true);
        Connection connection = at(NOON.plus(minutes(1)).get(), () -> factory.getConnection());

        assertNull("connection was not null", connection);
    }

    @Test
    public void shouldNotSetSslProtocolIfConfiguredNotToUseSsl() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);
        when(configWithToken.getUseMessagingSsl()).thenReturn(false);

        assertNotNull(at(NOON.get(), () -> factory.getConnection()));

        verify(rabbitConnectionFactory, never()).useSslProtocol(anyString());
    }

    @Test
    public void shouldAttemptToCreateConnectionWithFirstDiscoveredEnabledSslVersion() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);
        when(configWithToken.getUseMessagingSsl()).thenReturn(true);
        final val discoveredProtocol = "discoveredProtocol";
        when(sslProtocolsProvider.provideSupportedPrioritised())
            .thenReturn(Arrays.asList(discoveredProtocol));

        assertNotNull(at(NOON.get(), () -> factory.getConnection()));

        verify(rabbitConnectionFactory).useSslProtocol(discoveredProtocol);
    }

    @Test
    public void shouldNotAttemptToCreateConnectionWithSecondDiscoveredEnabledSslVersionIfFirstSucceeds()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);
        when(configWithToken.getUseMessagingSsl()).thenReturn(true);
        final val p1 = "protocol1";
        final val p2 = "protocol2";
        when(sslProtocolsProvider.provideSupportedPrioritised()).thenReturn(Arrays.asList(p1, p2));

        assertNotNull(at(NOON.get(), () -> factory.getConnection()));

        InOrder inOrder = inOrder(rabbitConnectionFactory);
        inOrder.verify(rabbitConnectionFactory).useSslProtocol(p1);
        inOrder.verify(rabbitConnectionFactory).newConnection(executorService);
        verify(rabbitConnectionFactory, never()).useSslProtocol(p2);
    }

    @Test
    public void shouldNotCreateConnectionWhenNoSslVersionsAreDiscovered() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);
        when(configWithToken.getUseMessagingSsl()).thenReturn(true);
        when(sslProtocolsProvider.provideSupportedPrioritised()).thenReturn(Collections.emptyList());

        assertNull(at(NOON.get(), () -> factory.getConnection()));
    }

    @Test
    public void shouldNotCreateConnectionIfWithTheOnlyDiscoveredSslVersionConnectionCreationFails()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenThrow(IOException.class);
        when(configWithToken.getUseMessagingSsl()).thenReturn(true);
        final val discoveredProtocol = "discoveredProtocol";
        when(sslProtocolsProvider.provideSupportedPrioritised())
            .thenReturn(Arrays.asList(discoveredProtocol));

        assertNull(at(NOON.get(), () -> factory.getConnection()));
    }

    @Test
    public void shouldAttemptToCreateConnectionWithSecondDiscoveredEnabledSslVersionIfFirstFails()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class)))
            .thenThrow(IOException.class)
            .thenReturn(openConnection);
        when(configWithToken.getUseMessagingSsl()).thenReturn(true);
        final val p1 = "firstProtocol";
        final val p2 = "secondProtocol";
        when(sslProtocolsProvider.provideSupportedPrioritised()).thenReturn(Arrays.asList(p1, p2));

        assertNotNull(at(NOON.get(), () -> factory.getConnection()));

        InOrder inOrder = inOrder(rabbitConnectionFactory);
        inOrder.verify(rabbitConnectionFactory).useSslProtocol(p1);
        inOrder.verify(rabbitConnectionFactory).newConnection(executorService);
        inOrder.verify(rabbitConnectionFactory).useSslProtocol(p2);
        inOrder.verify(rabbitConnectionFactory).newConnection(executorService);
    }

    @Test
    public void shouldCreateConnectionWithPreviouslySucceededTlsVersionWithoutUsingPreviouslyFailedOnes()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class)))
            .thenThrow(IOException.class)
            .thenReturn(openConnection);
        when(configWithToken.getUseMessagingSsl()).thenReturn(true);
        final val p1 = "firstProtocol";
        final val p2 = "secondProtocol";
        when(sslProtocolsProvider.provideSupportedPrioritised()).thenReturn(Arrays.asList(p1, p2));

        assertNotNull(at(NOON.get(), () -> factory.getConnection()));
        factory.close(false);
        assertNotNull(at(NOON.get(), () -> factory.getConnection()));

        InOrder inOrder = inOrder(rabbitConnectionFactory);
        inOrder.verify(rabbitConnectionFactory).useSslProtocol(p1);
        inOrder.verify(rabbitConnectionFactory).newConnection(executorService);
        inOrder.verify(rabbitConnectionFactory).useSslProtocol(p2);
        inOrder.verify(rabbitConnectionFactory).newConnection(executorService);
        inOrder.verify(rabbitConnectionFactory).useSslProtocol(p2);
        inOrder.verify(rabbitConnectionFactory).newConnection(executorService);
    }

    @Test
    public void shuttingFactoryDownShouldResultInInvokingConnectionStatusListener() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(false);

        verify(connectionStatusListener).onConnectionDown();
    }

    @Test
    public void failureToDispatchConnectionDownEventShouldBeLogged() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);
        final String dispatchFailureReason = "badListenerImplementation";
        doThrow(new RuntimeException(dispatchFailureReason))
            .when(connectionStatusListener)
            .onConnectionDown();

        at(NOON.get(), () -> factory.getConnection());
        factory.close(false);

        logsMock.verifyLoggedLineContaining("Problems dispatching onConnectionDown");
        logsMock.verifyLoggedExceptionMessageContaining(dispatchFailureReason);
    }

    @Test
    public void theFactThatApplicationShutsTheFactoryDownShouldBeIndicatedInLogs() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(false);

        logsMock.verifyLoggedLineContaining(CONNECTION_HAS_BEEN_SHUT_DOWN);
        logsMock.verifyLoggedLineContaining(INITIATED_BY_APPLICATION);
    }

    @Test
    public void rabbitMqDecidingToShutDownConnectionShouldResultInInvokingConnectionStatusListener()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        openConnection.getControlApi().closeInitiatedByRabbitMq();

        verify(connectionStatusListener).onConnectionDown();
    }

    @Test
    public void theFactThatApplicationShutsDownTheConnectionShouldNotBeLoggedIfRabbitMqDoesThatInstead()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        openConnection.getControlApi().closeInitiatedByRabbitMq();

        logsMock.verifyLoggedLineContaining(CONNECTION_HAS_BEEN_SHUT_DOWN);
        logsMock.verifyNotLoggedLineContaining(INITIATED_BY_APPLICATION);
    }

    @Test
    public void failureToDispatchConnectionDownEventOnConnectionBlockageShouldBeLogged() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);
        final String dispatchFailureReason = "badListenerImplementation";
        doThrow(new RuntimeException(dispatchFailureReason))
            .when(connectionStatusListener)
            .onConnectionDown();

        at(NOON.get(), () -> factory.getConnection());
        openConnection.getControlApi().blockDueTo(ANY);

        logsMock.verifyLoggedLineContaining("Problems dispatching onConnectionDown");
        logsMock.verifyLoggedExceptionMessageContaining(dispatchFailureReason);
    }

    @Test
    public void rabbitMqBlockingTheConnectionShouldResultInInvokingConnectionStatusListener()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        openConnection.getControlApi().blockDueTo(ANY);

        verify(connectionStatusListener).onConnectionDown();
    }

    @Test
    public void theFactThatRabbitMqBlockedTheConnectionShouldBeLoggedAlongWithTheReason() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        String reason = "congestion";
        openConnection.getControlApi().blockDueTo(reason);

        logsMock.verifyLoggedLineContaining("blocked");
        logsMock.verifyLoggedLineContaining(reason);
    }

    @Test
    public void rabbitMqUnblockingTheConnectionShouldResultInInvokingConnectionStatusListener()
        throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        openConnection.getControlApi().unblock();

        verify(connectionStatusListener).onConnectionDown();
        logsMock.verifyLoggedLineContaining("unblocked");
    }

    private Connection at(long epochMillis, Callable<Connection> factoryInvocation) throws Exception {
        when(timeUtils.now()).thenReturn(epochMillis);
        when(timeUtils.nowInstant()).thenReturn(Instant.ofEpochMilli(epochMillis));
        return factoryInvocation.call();
    }

    private static SdkInternalConfiguration createConfigWithToken() {
        SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        when(config.getAccessToken()).thenReturn("someAccessToken");
        return config;
    }
}
