/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.AmqpConnectionFactoryFake.initiallyProvides;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ChannelsToBeCreated.creating;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ConnectionHealth.HEALTHY;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ConnectionHealth.UNHEALTHY_AUTO_RECOVERING;
import static com.sportradar.utils.thread.sleep.SleepMock.onSleepDo;
import static com.sportradar.utils.time.TimeInterval.minutes;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.impl.MessageConsumer;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqSystemListener;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.RabbitMqChannelSupervisors.Builder;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.thread.sleep.Sleep;
import com.sportradar.utils.time.EpochMillis;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity", "HiddenField", "VariableDeclarationUsageDistance" })
public class RabbitMqChannelImplTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private static final EpochMillis MIDNIGHT = new EpochMillis(MIDNIGHT_TIMESTAMP_MILLIS);

    @Nested
    public class OnInitiation {

        private final TimeUtilsStub time = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));
        private final ChannelFixture channel = new ChannelFixture();
        private final Channel anyChannel = new NoOpRecoverableChannel();

        private final ConnectionToBeProvided.Factory connection = new ConnectionToBeProvided.Factory(time);

        @Test
        public void shouldNotBeInstantiatedWithNullTimeUtils() {
            assertThatThrownBy(() ->
                    new RabbitMqChannelImpl(
                        mock(RabbitMqSystemListener.class),
                        mock(WhoAmIReader.class),
                        "anyVersion",
                        mock(AmqpConnectionFactory.class),
                        null,
                        mock(Sleep.class)
                    )
                )
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void shouldNotBeInstantiatedWithNullSleep() {
            assertThatThrownBy(() ->
                    new RabbitMqChannelImpl(
                        mock(RabbitMqSystemListener.class),
                        mock(WhoAmIReader.class),
                        "anyVersion",
                        mock(AmqpConnectionFactory.class),
                        mock(TimeUtils.class),
                        null
                    )
                )
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void rabbitMqChannelShouldBeCreatedOnOpeningSupervisor() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            new RabbitMqChannelSupervisors.Builder().with(connectionFactory).opened();

            channel.verifyInitiatedTimes(1);
        }

        @Test
        public void rabbitMqChannelShouldNotBeCreatedIfConnectionWasUnavailableWhenOpeningSupervisor() {
            assertThatNoException()
                .isThrownBy(() -> {
                    new RabbitMqChannelSupervisors.Builder().with(mock(AmqpConnectionFactory.class)).opened();
                });
        }
    }

    @Nested
    public class OnInspection {

        private final TimeInterval idleInterval = minutes(3);
        private final TimeInterval lessThanIdleInterval = idleInterval.minus(seconds(1));
        private ListAppender<ILoggingEvent> appender;

        private final TimeUtilsStub time = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));

        private final ChannelFixture channel = new ChannelFixture();

        private final ConnectionToBeProvided.Factory connection = new ConnectionToBeProvided.Factory(time);

        @BeforeEach
        public void setup() throws Exception {
            // Sort out logging interception
            Logger logger = LoggerFactory.getLogger(RabbitMqChannelImpl.class);
            ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
            appender = new ListAppender<>();
            logbackLogger.addAppender(appender);
            appender.start();
        }

        @Test
        public void createsChannelIfIsNotCreatedYet()
            throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
            Connection connection = mock(Connection.class);
            when(connection.createChannel()).thenReturn(channel);
            AmqpConnectionFactory connectionFactory = mock(AmqpConnectionFactory.class);
            when(connectionFactory.getConnection()).thenReturn(null, connection);
            when(connectionFactory.canConnectionOpen()).thenReturn(true);
            val channelSupervisor = new Builder().with(connectionFactory).opened();

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(1);
        }

        @Test
        public void doesNotRecreateChannelOnEachInspection() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();

            channelSupervisor.checkStatus();
            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(1);
        }

        @Test
        public void doesNotRestartChannelIfItIsNotUsingStaleConnection() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(1);
        }

        @Test
        public void restartsChannelIfItIsUsingStaleConnection()
            throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            time.tick();
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));
            anotherChannelSupervisorRestartsConnection(connectionFactory);

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(2);
            channel.verifyClosedTimes(1);
        }

        @Test
        public void channelReceivesMessage()
            throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
            val consumer = new CountingMessagesConsumer();
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(consumer).with(connectionFactory).with(time).opened();
            time.tick();

            channel.sendMessage();

            consumer.verifyMessagesReceived(1);
        }

        @Test
        public void discardsMessagesReceivedFromChannelClosedDueToOperatingOnStaleConnection()
            throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
            val channelToBeClosed = new ChannelFixture();
            val consumer = new CountingMessagesConsumer();
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channelToBeClosed)));
            time.tick();
            val channelSupervisor = new Builder().with(consumer).with(connectionFactory).with(time).opened();
            time.tick();
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));
            anotherChannelSupervisorRestartsConnection(connectionFactory);

            channelSupervisor.checkStatus();
            channelToBeClosed.sendMessage();

            consumer.verifyNoMessagesReceived();
        }

        private void anotherChannelSupervisorRestartsConnection(AmqpConnectionFactoryFake connectionFactory)
            throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException {
            connectionFactory.close(false);
            connectionFactory.getConnection();
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void restartsChannelIfItHasNeverReceivedAnyMessagesAndIsIdleFor3minutes() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            time.tick(idleInterval);

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(2);
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void messagesReceivedFromClosedChannelsDoesNotResetCountingTowards3minutesThresholdOfIdleness()
            throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
            ChannelFixture channelToBeClosed = new ChannelFixture();
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channelToBeClosed)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            time.tick();
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));
            anotherChannelSupervisorRestartsConnection(connectionFactory);

            channelSupervisor.checkStatus();

            time.tick(idleInterval.minus(seconds(5)));
            channelToBeClosed.sendMessage();

            time.tick(seconds(5));
            channelSupervisor.checkStatus();

            channel.verifyClosedTimes(1);
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void notRestartsChannelIfItHasNeverReceivedAnyMessagesAndIsIdleForLessThan3minutes()
            throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            time.tick(lessThanIdleInterval);

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(1);
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void notRestartsChannelWhichHasEverReceivedMessagesButIsIdleFor3minutesIfConnectionIsAutoRecovering()
            throws IOException {
            val connectionFactory = initiallyProvides(
                connection.whichIs(UNHEALTHY_AUTO_RECOVERING, creating(channel))
            );
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(1);
            channel.verifyClosedTimes(0);
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void restartsChannelAlongWithConnectionIfChannelHasEverReceivedAnyMessagesButIsIdleFor3minutes()
            throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick(seconds(1));
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(2);
            assertThat(connectionFactory.getConnectionStarted())
                .isEqualTo(MIDNIGHT.plus(idleInterval.plus(seconds(1))).get());
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void notRestartsNeitherChannelNorConnectionIfChannelHasEverReceivedMessagesButIsIdleForLessThan3minutes()
            throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(lessThanIdleInterval);
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));

            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(1);
            assertThat(connectionFactory.getConnectionStarted()).isEqualTo(MIDNIGHT.get());
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void leavesSomeTimeGapBetweenRestartingConnectionAndRestartingChannel() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            Sleep sleep = onSleepDo(() -> {
                assertThat(connectionFactory.hasConnection()).isFalse();
                assertThat(channel.isOpen()).isFalse();
            });
            val channelSupervisor = new Builder().with(connectionFactory).with(time).with(sleep).opened();
            channel.sendMessage();
            time.tick(idleInterval);

            channelSupervisor.checkStatus();

            verify(sleep).millis(5000);
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void idleFor3MinutesChannelWhichHasEverReceivedAnyMessagesCausesHealthyConnectionToBeRestarted()
            throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);

            channelSupervisor.checkStatus();

            assertThat(connectionFactory.hasConnection()).isFalse();
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void leaveUnhealthyConnectionToAutoRecoverIfChannelHasEverReceivedAnyMessagesButIsIdleFor3minutes()
            throws IOException {
            val connectionFactory = initiallyProvides(
                connection.whichIs(UNHEALTHY_AUTO_RECOVERING, creating(channel))
            );
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);

            channelSupervisor.checkStatus();

            assertThat(connectionFactory.hasConnection()).isTrue();
            assertThat(connectionFactory.isConnectionHealthy()).isFalse();
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void restartsConnectionIfItBecomesHealthyWhenChannelHasEverReceivedAnyMessagesButIsIdleOver3minutes()
            throws IOException {
            val connection = this.connection.whichIs(UNHEALTHY_AUTO_RECOVERING, creating(channel));
            val connectionFactory = initiallyProvides(connection);
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);
            channelSupervisor.checkStatus();

            connection.setHealth(HEALTHY);
            channelSupervisor.checkStatus();

            assertThat(connectionFactory.hasConnection()).isFalse();
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void notClosesAlreadyClosedConnectionIfChannelHasEverReceivedAnyMessagesButIsIdleFor3minutes()
            throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);
            connectionFactory.close(false);

            channelSupervisor.checkStatus();

            connectionFactory.assertThatAlreadyClosedConnectionWasNotClosedAgain();
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void afterRestartingConnectionNotEntersIntoConnectionRestartingLoop() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick(seconds(1));
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));

            channelSupervisor.checkStatus();
            channelSupervisor.checkStatus();

            assertThat(connectionFactory.hasConnection()).isTrue();
            assertThat(connectionFactory.getConnectionStarted())
                .isEqualTo(MIDNIGHT.plus(idleInterval.plus(seconds(1))).get());
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void afterRestartingConnectionAlongWithChannelNotEnterIntoChannelRestartingLoop()
            throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));

            channelSupervisor.checkStatus();
            channelSupervisor.checkStatus();

            channel.verifyInitiatedTimes(2);
            channel.verifyClosedTimes(1);
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void connectionIsRestartedAgainIfTheUnfavourableScenarioHappensInSubsequent3Minutes()
            throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)));
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);
            connectionFactory.afterClosingStarts(connection.whichIs(creating(channel)));
            channelSupervisor.checkStatus();
            channel.sendMessage();
            time.tick(idleInterval);
            connectionFactory.afterClosingStarts(connection.absent());

            channelSupervisor.checkStatus();

            assertThat(connectionFactory.hasConnection()).isFalse();
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void connectionClosureDueToIoExceptionShouldBeLogged() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)))
                .onCloseThrowing(new IOException());
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);

            channelSupervisor.checkStatus();

            assertContainsLogLine("Error closing connection:");
        }

        @Test
        @SuppressWarnings("MagicNumber")
        public void connectionClosureDueToRuntimeExceptionShouldBeLogged() throws IOException {
            val connectionFactory = initiallyProvides(connection.whichIs(creating(channel)))
                .onCloseThrowing(new IllegalArgumentException());
            time.tick();
            val channelSupervisor = new Builder().with(connectionFactory).with(time).opened();
            channel.sendMessage();
            time.tick(idleInterval);

            channelSupervisor.checkStatus();

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

        public class CountingMessagesConsumer implements ChannelMessageConsumer {

            private int count;

            @Override
            public void open(MessageConsumer messageConsumer) {}

            @Override
            public void onMessageReceived(
                String routingKey,
                byte[] body,
                AMQP.BasicProperties properties,
                long receivedAt
            ) {
                count++;
            }

            @Override
            public String getConsumerDescription() {
                return null;
            }

            @Override
            public void close() throws IOException {}

            public void verifyNoMessagesReceived() {
                assertThat(count).isZero();
            }

            public void verifyMessagesReceived(int expectedMessageCount) {
                assertThat(count).isEqualTo(expectedMessageCount);
            }
        }
    }
}
