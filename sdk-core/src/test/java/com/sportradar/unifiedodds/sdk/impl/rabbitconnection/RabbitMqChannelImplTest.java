package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqSystemListener;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMqChannelImplTest {

    private static EpochMillis MIDNIGHT = new EpochMillis(1664402400000L);
    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    private AMQPConnectionFactory connectionFactory;

    private RabbitMqChannelImpl rabbitMqChannel;
    private ScheduledFuture<?> scheduledSupervision;

    private TimeUtils timeUtils = mock(TimeUtils.class);

    private ChannelFixture channel = new ChannelFixture.Holder(timeUtils).get();

    public RabbitMqChannelImplTest() throws IOException {
    }

    //private Thread monitorThread;

    @Before
    public void setup() throws Exception {
        // Sort out logging interception
        logger = LoggerFactory.getLogger(RabbitMqChannelImpl.class);
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        appender = new ListAppender();
        logbackLogger.addAppender(appender);
        appender.start();

        RabbitMqSystemListener rabbitMqSystemListener = mock(RabbitMqSystemListener.class);
        WhoAmIReader whoAmIReader = mock(WhoAmIReader.class);
        String sdkVersion = "1";
        connectionFactory = mock(AMQPConnectionFactory.class);
        RecoverableChannel channel = mock(RecoverableChannel.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connectionFactory.isConnectionOpen()).thenReturn(true);
        when(connectionFactory.canConnectionOpen()).thenReturn(true);
        when(connection.createChannel()).thenReturn(channel);
        AMQP.Queue.DeclareOk declareOk = mock(AMQP.Queue.DeclareOk.class);
        when(channel.queueDeclare()).thenReturn(declareOk);
        when(declareOk.getQueue()).thenReturn("queue");
        ChannelMessageConsumer channelMessageConsumer = mock(ChannelMessageConsumer.class);
        rabbitMqChannel = new RabbitMqChannelImpl(rabbitMqSystemListener, whoAmIReader, sdkVersion, connectionFactory, new TimeUtilsImpl());
        ArrayList<String> routingKeys = new ArrayList<String>();
        routingKeys.add("-.-.-.snapshot_complete.-.-.-.-");
        scheduledSupervision = new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> rabbitMqChannel.checkStatus(), 20L, 20L, TimeUnit.SECONDS);
        rabbitMqChannel.open(routingKeys, channelMessageConsumer, "SystemMessages");

        //monitorThread = new Thread(rabbitMqChannel::checkChannelStatus);
        //monitorThread.setUncaughtExceptionHandler(myUncaughtExceptionHandler);
    }

    @After
    public void tearDown() {
        if (scheduledSupervision != null) {
            scheduledSupervision.cancel(false);
        }
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotBeInstantiatedWithNullTimeUtils() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "anyVersion", mock(AMQPConnectionFactory.class), null);
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

    private void setChannelLastMessage() throws Exception {
        // Now set message time
        LocalDateTime now = LocalDateTime.now();
        Field f1 = rabbitMqChannel.getClass().getDeclaredField("channelLastMessage");
        f1.setAccessible(true);
        f1.set(rabbitMqChannel, now);
        LocalDateTime str1 = (LocalDateTime) f1.get(rabbitMqChannel);
        System.out.println("field: " + str1);
    }

    @Test
    public void test_180s_connection_factory_close_with_io_exception() throws Exception {
        doThrow(new IOException("eek - ioexception")).when(connectionFactory).close(false);

        setChannelLastMessage();

        // Need to wait more than 180 seconds to see the connection reconnection logic
        Thread.sleep(220000);
        rabbitMqChannel.close();

        assertContainsLogLine("Error closing connection:");
    }

    @Test
    public void test_180s_connection_factory_close_with_runtime_exception() throws Exception {
        doThrow(new RuntimeException("eek - runtimeexception")).when(connectionFactory).close(false);

        setChannelLastMessage();

        // Need to wait more than 180 seconds to see the connection reconnection logic
        Thread.sleep(220000);
        rabbitMqChannel.close();

        assertContainsLogLine("Error closing connection");
    }

    @Test
    public void test_180s_connection_factory_already_closed() throws Exception {
        doThrow(new RuntimeException("eek - runtimeexception")).when(connectionFactory).close(false);
        when(connectionFactory.isConnectionOpen()).thenReturn(false);

        setChannelLastMessage();

        // Need to wait more than 180 seconds to see the connection reconnection logic
        Thread.sleep(220000);
        rabbitMqChannel.close();

        assertDoesNotContainLogLine("Error closing connection:");
    }

    @Test
    public void rabbitMqChannelShouldBeCreatedOnOpeningSupervisor() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AMQPConnectionFactory connectionFactory = AMQPConnectionFactoryStub.holdingConnectionCreatedAt(MIDNIGHT.get(), connection);
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, mock(TimeUtils.class));

        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldNotBeCreatedIfConnectionWasUnavailableWhenOpeningSupervisor() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        AMQPConnectionFactory connectionFactory = mock(AMQPConnectionFactory.class);
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, mock(TimeUtils.class));

        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");

        //ensuring no exception is thrown
    }

    @Test
    public void rabbitMqChannelShouldBeCreatedOnInspectionIfChannelIsNotYetCreated() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AMQPConnectionFactory connectionFactory = mock(AMQPConnectionFactory.class);
        when(connectionFactory.getConnection()).thenReturn(null, connection);
        when(connectionFactory.canConnectionOpen()).thenReturn(true);
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, mock(TimeUtils.class));
        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");

        supervisor.checkStatus();

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldNotBeCreatedOnInspectionIfChannelIsAlreadyCreated() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AMQPConnectionFactory connectionFactory = AMQPConnectionFactoryStub.holdingConnectionCreatedAt(MIDNIGHT.get(), connection);
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, timeUtils);
        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");


        supervisor.checkStatus();

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldNotBeRestartedOnInspectionIfItIsNotUsingStaleConnection() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AMQPConnectionFactory connectionFactory = AMQPConnectionFactoryStub.holdingConnectionCreatedAt(MIDNIGHT.get(), connection);
        TimeUtils timeUtils = mock(TimeUtils.class);
        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, timeUtils);
        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");


        supervisor.checkStatus();

        channel.verifyInitiated(times(1));
    }

    @Test
    public void rabbitMqChannelShouldBeRestartedOnInspectionIfItIsUsingStaleConnection() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AMQPConnectionFactory connectionFactory = AMQPConnectionFactoryStub.holdingConnectionCreatedAt(MIDNIGHT.get(), connection);
        TimeUtils timeUtils = mock(TimeUtils.class);
        when(timeUtils.now()).thenReturn(MIDNIGHT.minusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, timeUtils);
        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");

        supervisor.checkStatus();

        channel.verifyInitiated(times(2));
    }

    @Test
    public void rabbitMqChannelShouldBeRestartedOnInspectionIfItHasNeverEverReceivedAnyMessageAndIsIdleFor3minutes() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AMQPConnectionFactory connectionFactory = AMQPConnectionFactoryStub.holdingConnectionCreatedAt(MIDNIGHT.get(), connection);
        long pastMidnight1Minute = MIDNIGHT.plusMinutes(1);
        long pastMidnight4Minutes = MIDNIGHT.plusMinutes(4);
        TimeUtils timeUtils = mock(TimeUtils.class);
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, timeUtils);
        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");
        when(timeUtils.now()).thenReturn(pastMidnight1Minute, pastMidnight4Minutes);

        supervisor.checkStatus();

        channel.verifyInitiated(times(2));
    }

    @Test
    public void rabbitMqChannelShouldBeRestartedOnInspectionIfItHasEverReceivedAMessageButIsIdleFor3minutes() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        when(connection.createChannel()).thenReturn(channel);
        AMQPConnectionFactory connectionFactory = AMQPConnectionFactoryStub.holdingConnectionCreatedAt(MIDNIGHT.get(), connection);


        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(1));
        OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(mock(RabbitMqSystemListener.class), mock(WhoAmIReader.class), "any", connectionFactory, timeUtils);
        supervisor.open(Arrays.asList("any"), mock(ChannelMessageConsumer.class), "any");
        ArgumentCaptor<Consumer> messageConsumer = ArgumentCaptor.forClass(Consumer.class);

        channel.sendMessageAt(MIDNIGHT.plusMinutes(4));

        when(timeUtils.now()).thenReturn(MIDNIGHT.plusMinutes(7));

        supervisor.checkStatus();

        channel.verifyInitiated(times(2));
    }


}