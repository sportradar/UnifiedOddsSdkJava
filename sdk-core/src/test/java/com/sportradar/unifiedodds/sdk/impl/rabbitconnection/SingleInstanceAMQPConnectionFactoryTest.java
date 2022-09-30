package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.SDKConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SingleInstanceAMQPConnectionFactoryTest {

    private static final EpochMillis NOON = new EpochMillis(1664445600000L);
    private static final long NO_CONNECTION_TIMESTAMP = 0L;
    private Connection openConnection = mock(Connection.class);
    private SDKInternalConfiguration suitableConfig = createSuitableConfig();
    private SingleInstanceAMQPConnectionFactory.FirewallChecker firewallChecker = mock(SingleInstanceAMQPConnectionFactory.FirewallChecker.class);
    private TimeUtils timeUtils = mock(TimeUtils.class);
    private ConnectionFactory rabbitConnectionFactory = mock(ConnectionFactory.class);
    private AMQPConnectionFactory factory = new SingleInstanceAMQPConnectionFactory(rabbitConnectionFactory, suitableConfig, mock(SDKConnectionStatusListener.class), "any", mock(WhoAmIReader.class), mock(ExecutorService.class), firewallChecker, timeUtils);

    @Before
    public void setup() {
        when(openConnection.isOpen()).thenReturn(true);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotInstantiateWithoutFirewallChecker() {
        new SingleInstanceAMQPConnectionFactory(mock(ConnectionFactory.class), suitableConfig, mock(SDKConnectionStatusListener.class), "any", mock(WhoAmIReader.class), mock(ExecutorService.class), null, timeUtils);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotInstantiateWithoutTimeUtils() {
        new SingleInstanceAMQPConnectionFactory(mock(ConnectionFactory.class), suitableConfig, mock(SDKConnectionStatusListener.class), "any", mock(WhoAmIReader.class), mock(ExecutorService.class), firewallChecker, null);
    }

    @Test
    public void initiallyNoConnectionShouldBeCreated() {
        assertFalse(factory.isConnectionOpen());
        assertEquals(0, factory.getConnectionStarted());
        assertTrue(factory.canConnectionOpen());
    }

    @Test
    public void closingFactoryBeforeFirstConnectionIsCreatedAndIndicatingFeedIsStillOpenShouldHaveNoEffect() throws IOException {
        factory.close(false);

        assertFalse(factory.isConnectionOpen());
        assertEquals(0, factory.getConnectionStarted());
        assertTrue(factory.canConnectionOpen());
    }

    @Test
    public void shouldBeClosableAlongWithFeedClosureEvenBeforeBeforeFirstConnectionIsCreated() throws IOException {
        factory.close(true);

        assertFalse(factory.isConnectionOpen());
        assertEquals(0, factory.getConnectionStarted());
        assertFalse(factory.canConnectionOpen());
    }


    @Test
    public void creatingConnectionShouldCheckFirewall() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        when(suitableConfig.getAPIHost()).thenReturn("https://sportradar.com");

        factory.getConnection();

        verify(firewallChecker).checkFirewall("https://sportradar.com");
    }

    @Test
    public void shouldCreateConnectionOnInitialRetrieval() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        Connection connection = at(NOON.get(), () -> factory.getConnection());

        assertNotNull("connection was null", connection);
        assertTrue("connection should be open", factory.isConnectionOpen());
        assertEquals(NOON.get(), factory.getConnectionStarted());
        assertTrue("connection should be able to open", factory.canConnectionOpen());
    }

    @Test
    public void shouldNotCreateConnectionIfConnectionHasAlreadyBeenCreated() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        Connection connection = at(NOON.plusMinutes(1), () -> factory.getConnection());

        assertNotNull("connection was null", connection);
        assertEquals(NOON.get(), factory.getConnectionStarted());
        verify(rabbitConnectionFactory, times(1)).newConnection(any(ExecutorService.class));
    }

    @Test
    public void connectionShouldBeClosedAfterClosingTheFactoryIndicatingFeedIsStillOpen() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(false);

        assertFalse("connection should be closed", factory.isConnectionOpen());
        assertEquals(NO_CONNECTION_TIMESTAMP, factory.getConnectionStarted());
        assertTrue("connection should be able to open", factory.canConnectionOpen());
    }

    @Test
    public void connectionShouldBeClosedAfterClosingTheFactoryIndicatingFeedIsClosedAsWell() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(true);

        assertFalse("connection should be closed", factory.isConnectionOpen());
        assertEquals(NO_CONNECTION_TIMESTAMP, factory.getConnectionStarted());
        assertFalse("connection should not be able to open", factory.canConnectionOpen());
    }

    @Test
    public void shouldCreateConnectionAfterClosingFactory() throws Exception {
        when(rabbitConnectionFactory.newConnection(any(ExecutorService.class))).thenReturn(openConnection);

        at(NOON.get(), () -> factory.getConnection());
        factory.close(false);
        Connection connection = at(NOON.plusMinutes(1), () -> factory.getConnection());

        assertNotNull("connection was null", connection);
        assertTrue("connection should be open", factory.isConnectionOpen());
        assertEquals(NOON.plusMinutes(1), factory.getConnectionStarted());
        assertTrue("connection should be able to open", factory.canConnectionOpen());
    }

    private Connection at(long epochMillis, Callable<Connection> factoryInvocation) throws Exception {
        when(timeUtils.now()).thenReturn(epochMillis);
        return factoryInvocation.call();
    }

    private static SDKInternalConfiguration createSuitableConfig() {
        SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
        when(config.getAPIHost()).thenReturn("https://sportradar.com");
        when(config.getAccessToken()).thenReturn("someAccessToken");
        return config;
    }
}