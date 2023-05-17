package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import java.io.IOException;
import org.junit.Test;
import org.mockito.exceptions.verification.TooManyActualInvocations;
import org.mockito.exceptions.verification.WantedButNotInvoked;

@SuppressWarnings({ "MagicNumber" })
public class ChannelFixtureTest {

    private TimeUtils timeUtils = mock(TimeUtils.class);

    @Test
    public void shouldDeclareQueue() throws IOException {
        RecoverableChannel channel = new ChannelFixture.Holder(mock(TimeUtils.class)).get();

        assertNotNull(channel.queueDeclare());
        assertNotNull(channel.queueDeclare().getQueue());
    }

    @Test
    public void shouldSendMessage() throws IOException {
        ChannelFixture channel = new ChannelFixture.Holder(mock(TimeUtils.class)).get();
        TestConsumer consumer = initiateChannel(channel);

        channel.sendMessageAt(1L);

        assertTrue("message not processed", consumer.isProcessed);
    }

    @Test
    public void sendingMessageShouldUpdateTime() throws IOException {
        long time = 1664402400000L;
        ChannelFixture channel = new ChannelFixture.Holder(timeUtils).get();
        TestConsumer consumer = initiateChannel(channel);

        channel.sendMessageAt(time);

        assertEquals("message not processed", time, consumer.currentTimeEpochMillis);
    }

    @Test
    public void shouldVerifyChannelWasInitiated() throws IOException {
        ChannelFixture channel = new ChannelFixture.Holder(timeUtils).get();
        initiateChannel(channel);

        channel.verifyInitiated(times(1));
    }

    @Test(expected = WantedButNotInvoked.class)
    public void shouldFailVerificationIfChannelWasNotInitiated() throws IOException {
        ChannelFixture channel = new ChannelFixture.Holder(timeUtils).get();

        channel.verifyInitiated(times(1));
    }

    @Test(expected = TooManyActualInvocations.class)
    public void shouldFailVerificationIfChannelWasInitiatesMoreTimesThanExpected() throws IOException {
        ChannelFixture channel = new ChannelFixture.Holder(timeUtils).get();
        initiateChannel(channel);
        initiateChannel(channel);

        channel.verifyInitiated(times(1));
    }

    private TestConsumer initiateChannel(ChannelFixture channel) throws IOException {
        TestConsumer consumer = new TestConsumer(channel, timeUtils);
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
