package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.EpochMillis;
import com.sportradar.utils.time.TimeUtilsStub;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

@SuppressWarnings({ "MagicNumber" })
public class ChannelFixtureTest {

    private TimeUtils time = mock(TimeUtils.class);

    @Test
    public void shouldDeclareQueue() throws IOException {
        NoOpRecoverableChannel channel = new ChannelFixture();

        assertNotNull(channel.queueDeclare());
        assertNotNull(channel.queueDeclare().getQueue());
    }

    @Test
    public void shouldSendMessage() throws IOException {
        ChannelFixture channel = new ChannelFixture();
        TestConsumer consumer = initiateChannelWithConsumer(channel);
        when(time.now()).thenReturn(1664402400001L);

        channel.sendMessage();

        assertTrue("message not processed", consumer.isProcessed);
    }

    @Test
    public void sendingMessageShouldUpdateTime() throws IOException {
        long aMoment = 1664402400000L;
        ChannelFixture channel = new ChannelFixture();
        TestConsumer consumer = initiateChannelWithConsumer(channel);

        when(time.now()).thenReturn(aMoment);
        channel.sendMessage();

        assertEquals("message not processed", aMoment, consumer.currentTimeEpochMillis);
    }

    @Test
    public void shouldVerifyChannelWasInitiated() throws IOException {
        ChannelFixture channel = new ChannelFixture();
        initiateChannelWithConsumer(channel);

        channel.verifyInitiatedTimes(1);
    }

    @Test
    public void shouldFailVerificationIfChannelWasNotInitiated() {
        ChannelFixture channel = new ChannelFixture();

        assertThatThrownBy(() -> channel.verifyInitiatedTimes(1)).isInstanceOf(AssertionFailedError.class);
    }

    @Test
    public void shouldFailVerificationIfChannelWasInitiatesMoreTimesThanExpected()
        throws IOException, TimeoutException {
        ChannelFixture channel = new ChannelFixture();
        initiateChannelWithConsumer(channel);
        channel.close();
        initiateChannelWithConsumer(channel);

        assertThatThrownBy(() -> channel.verifyInitiatedTimes(1)).isInstanceOf(AssertionFailedError.class);
    }

    @Test
    public void shouldFailToInitiateAlreadyInitiatedChannel() throws IOException, TimeoutException {
        ChannelFixture channel = new ChannelFixture();
        initiateChannelWithConsumer(channel);
        assertThatThrownBy(() -> initiateChannelWithConsumer(channel))
            .isInstanceOf(IllegalStateException.class);
    }

    private TestConsumer initiateChannelWithConsumer(ChannelFixture channel) throws IOException {
        TestConsumer consumer = new TestConsumer(channel, time);
        channel.basicConsume("any", true, "any", consumer);
        return consumer;
    }

    class TestConsumer extends DefaultConsumer {

        private boolean isProcessed;

        private long currentTimeEpochMillis;
        private TimeUtils timeUtils;

        public TestConsumer(Channel channel, TimeUtils timeUtils) {
            super(channel);
            this.timeUtils = timeUtils;
        }

        @Override
        public void handleDelivery(
            String consumerTag,
            Envelope envelope,
            AMQP.BasicProperties properties,
            byte[] body
        ) throws IOException {
            isProcessed = true;
            currentTimeEpochMillis = timeUtils.now();
        }
    }
}
