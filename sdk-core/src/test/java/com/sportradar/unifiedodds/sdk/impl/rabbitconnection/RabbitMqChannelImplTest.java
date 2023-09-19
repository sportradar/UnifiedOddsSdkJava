/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqSystemListener;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity", "HiddenField" })
public class RabbitMqChannelImplTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private static final String ANY = "any";
    private static final EpochMillis MIDNIGHT = new EpochMillis(MIDNIGHT_TIMESTAMP_MILLIS);
    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    private TimeUtils timeUtils = mock(TimeUtils.class);

    private ChannelFixture channel = new ChannelFixture.Holder(timeUtils).get();

    public RabbitMqChannelImplTest() throws IOException {}

    @Before
    public void setup() throws Exception {
        // Sort out logging interception
        logger = LoggerFactory.getLogger(RabbitMqChannelImpl.class);
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        appender = new ListAppender();
        logbackLogger.addAppender(appender);
        appender.start();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotBeInstantiatedWithNullTimeUtils()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            "anyVersion",
            mock(AmqpConnectionFactory.class),
            null
        );
    }

    @Test
    public void rabbitMqChannelShouldBeCreatedOnOpeningSupervisor()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            mock(TimeUtils.class)
        );

        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldNotBeCreatedIfConnectionWasUnavailableWhenOpeningSupervisor()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        AmqpConnectionFactory connectionFactory = mock(AmqpConnectionFactory.class);
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            mock(TimeUtils.class)
        );

        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        //ensuring no exception is thrown
    }

    @Test
    public void rabbitMqChannelShouldBeCreatedOnInspectionIfChannelIsNotYetCreated()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = mock(AmqpConnectionFactory.class);
        when(connectionFactory.getConnection()).thenReturn(null, connection);
        when(connectionFactory.canConnectionOpen()).thenReturn(true);
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            mock(TimeUtils.class)
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);

        supervisor.checkStatus();

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldNotBeCreatedOnInspectionIfChannelIsAlreadyCreated()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);

        supervisor.checkStatus();

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldNotBeRestartedOnInspectionIfItIsNotUsingStaleConnection()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);

        supervisor.checkStatus();

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldBeRestartedOnInspectionIfItIsUsingStaleConnection()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        when(timeUtils.now()).thenReturn(MIDNIGHT.minusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);

        supervisor.checkStatus();

        channel.verifyInitiated(times(2));
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void rabbitMqChannelShouldBeRestartedOnInspectionIfItHasNeverEverReceivedAnyMessageAndIsIdleFor3minutes()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        long pastMidnight1Minute = MIDNIGHT.plusMinutes(1);
        long pastMidnight4Minutes = MIDNIGHT.plusMinutes(4);
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        when(timeUtils.now()).thenReturn(pastMidnight1Minute, pastMidnight4Minutes);

        supervisor.checkStatus();

        channel.verifyInitiated(times(2));
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldBeRestartedOnInspectionIfItHasEverReceivedSomeMessageButIsIdleFor3minutesAndConnectionIsClosed()
        throws IOException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        anotherChannelCloses(connectionFactory);
        channel.sendMessageAt(MIDNIGHT.plusMinutes(4));
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(7));

        supervisor.checkStatus();

        channel.verifyInitiated(times(2));
    }

    private static void anotherChannelCloses(AmqpConnectionFactory connectionFactory) throws IOException {
        connectionFactory.close(false);
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldBeRestartedOnInspectionIfItHasEverReceivedSomeMessageButIsIdleFor3minutesAndConnectionIsOpen()
        throws IOException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        channel.sendMessageAt(MIDNIGHT.plusMinutes(4));
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(7));

        supervisor.checkStatus();

        channel.verifyInitiated(times(2));
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldCloseConnectionOnInspectionIfItHasEverReceivedSomeMessageButIsIdleFor3minutesAndConnectionIsOpen()
        throws IOException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        channel.sendMessageAt(MIDNIGHT.plusMinutes(4));
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(7));

        supervisor.checkStatus();

        assertFalse("connection should have been closed", connectionFactory.isConnectionOpen());
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void shouldNotCloseAlreadyClosedConnectionOnInspectionIfItHasEverReceivedSomeMessageButIsIdleFor3minutes()
        throws IOException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            MIDNIGHT.get(),
            connection
        );
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        channel.sendMessageAt(MIDNIGHT.plusMinutes(4));
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(7));

        supervisor.checkStatus();

        assertFalse("connection should have been closed", connectionFactory.isConnectionOpen());
        assertDoesNotContainLogLine("Error closing connection:");
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void connectionClosureDueToIoExceptionShouldBeLogged() throws IOException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs
            .holdingConnectionCreatedAt(MIDNIGHT.get(), connection)
            .onCloseThrowing(new IOException());
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        channel.sendMessageAt(MIDNIGHT.plusMinutes(4));
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(7));

        supervisor.checkStatus();

        assertContainsLogLine("Error closing connection:");
    }

    @Test
    @SuppressWarnings("MagicNumber")
    public void connectionClosureDueToRuntimeExceptionShouldBeLogged() throws IOException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AmqpConnectionFactory connectionFactory = AmqpConnectionFactoryStubs
            .holdingConnectionCreatedAt(MIDNIGHT.get(), connection)
            .onCloseThrowing(new IllegalArgumentException());
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
            mock(RabbitMqSystemListener.class),
            mock(WhoAmIReader.class),
            ANY,
            connectionFactory,
            timeUtils
        );
        supervisor.open(Arrays.asList(ANY), mock(ChannelMessageConsumer.class), ANY);
        channel.sendMessageAt(MIDNIGHT.plusMinutes(4));
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(7));

        supervisor.checkStatus();

        assertContainsLogLine("Error closing connection:");
    }

    private void assertContainsLogLine(final String text) {
        for (ILoggingEvent loggingEvent : appender.list) {
            if (loggingEvent.getFormattedMessage().contains(text)) {
                return;
            }
        }
        fail("Could not find log line that matches: " + text);
    }

    private void assertDoesNotContainLogLine(final String text) {
        for (ILoggingEvent loggingEvent : appender.list) {
            if (loggingEvent.getFormattedMessage().contains(text)) {
                fail("Found log line that matches: " + text);
            }
        }
    }
}
