package com.sportradar.unifiedodds.sdk.impl;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Recoverable;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMqChannelImplTest {
    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    private AMQPConnectionFactory connectionFactory;

    private RabbitMqChannelImpl rabbitMqChannel;

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
        ChannelRecoverable channel = mock(ChannelRecoverable.class);
        Connection connection = mock(Connection.class);
        when(connectionFactory.getConnection()).thenReturn(connection);
        when(connectionFactory.isConnectionOpen()).thenReturn(true);
        when(connectionFactory.canConnectionOpen()).thenReturn(true);
        when(connection.createChannel()).thenReturn(channel);
        AMQP.Queue.DeclareOk declareOk = mock(AMQP.Queue.DeclareOk.class);
        when(channel.queueDeclare()).thenReturn(declareOk);
        when(declareOk.getQueue()).thenReturn("queue");
        ChannelMessageConsumer channelMessageConsumer = mock(ChannelMessageConsumer.class);
        rabbitMqChannel = new RabbitMqChannelImpl(rabbitMqSystemListener, whoAmIReader, sdkVersion, connectionFactory);
        ArrayList<String> routingKeys = new ArrayList<String>();
        routingKeys.add("-.-.-.snapshot_complete.-.-.-.-");
        rabbitMqChannel.open(routingKeys, channelMessageConsumer, "SystemMessages");
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

    public interface ChannelRecoverable extends Channel, Recoverable {
    }
}